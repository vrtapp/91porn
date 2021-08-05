package com.jw.porn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class BeanSeederServices {

    @Bean
    public RetryTemplate retryTemplate() {
        //设置重试策略  最大重试次数4次（什么时候重试）,默认遇到Exception异常时重试。
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(4);

        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        // 设置重试间隔时间6S
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(6000);
        template.setBackOffPolicy(fixedBackOffPolicy);
        return template;
    }
}