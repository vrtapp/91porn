package com.jw.porn.utils;

import com.jw.porn.JavaScriptInterface;
import org.springframework.core.io.ClassPathResource;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;

/**
 * @program: demo
 * @description:
 * @author: Jia Wei
 * @create: 2021-07-14 17:04
 **/
public class JsUtil {

    public static String strencode(String str1) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        try {
            ClassPathResource resource = new ClassPathResource("md2.js");
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"));
            engine.eval(br);
        } catch (ScriptException e) {
            //忽略js脚本异常
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (engine instanceof Invocable) {
            Invocable invocable = (Invocable) engine;
            JavaScriptInterface executeMethod = invocable.getInterface(JavaScriptInterface.class);
            return executeMethod.strencode2(str1);
        }
        throw new RuntimeException("解密失敗");
    }

}



