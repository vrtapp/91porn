package com.jw.porn;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jw.porn.utils.DealStrSub;
import com.jw.porn.utils.JsUtil;
import com.jw.porn.utils.SpringUtil;
import com.jw.porn.utils.VideoUtils;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.jw.porn.PornApplication.*;
import static com.jw.porn.config.MyappConfig.*;
import static com.jw.porn.utils.WebUtil.UnitPage;


/**
 * @author jw131
 */
@Slf4j
@Service
public class MyAmazingBot extends TelegramLongPollingBot {



    public MyAmazingBot() {
    }

    public MyAmazingBot(DefaultBotOptions options) {
        super(options);
    }


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }
    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        RetryTemplate retryTemplate = SpringUtil.getBean(RetryTemplate.class);
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            SendChatAction sendChatAction = new SendChatAction();
            String chatId = update.getMessage().getChatId().toString();
            sendChatAction.setChatId(chatId);

            if (text.equals("/start")) {
                senTextMsg(chatId, "向我发送91视频链接，下载视频,视频最大50M");
                sendChatAction.setAction(ActionType.TYPING);
            }  else {
                retryTemplate.execute(context -> {
                    downloadVideo(text, sendChatAction, chatId);
                    return null;
                },retryContext -> {
                    senTextMsg(chatId,"解析错误，请检查地址是否正确");
                    return null;
                });

            }

            execute(sendChatAction);

        }
    }

    public void sendVideo2( String JpgPath, String FileRoot, String shoucang,String chatId) throws IOException, EncoderException, TelegramApiException {


        File file = new File(FileRoot);
        File[] filesInit = file.listFiles();
        String exegc = "^.+" + "mp4" + "$";
        List<File> files = Arrays.stream(filesInit).sorted().collect(Collectors.toList());
        for (int i = 0; i < files.size(); i++) {
            String name = files.get(i).getName();
            if(name.matches(exegc)){
                log.info("包含mp4"+name+",发送.....................");
                File mediaFile = new File(FileRoot + name);
                InputFile video = new InputFile(mediaFile);
                MultimediaInfo videoInfo = VideoUtils.getVideoInfo(mediaFile);
                int height = videoInfo.getVideo().getSize().getHeight();
                int width = videoInfo.getVideo().getSize().getWidth();
                //秒
                Integer integer = Integer.valueOf((int) Math.ceil(videoInfo.getDuration() / 1000));
                InputFile inputFile = new InputFile(new File(JpgPath));
                SendVideo build = SendVideo.builder().
                        chatId(CHAT_ID)
                        .video(video)
                        .duration(integer)
                        .height(height)
                        .width(width)
                        .supportsStreaming(true)
                        .thumb(inputFile)
                        .caption(name + shoucang)
                        .build();
                if (chatId!=null){
                    build.setChatId(chatId);
                }
                execute(build);


            }
        }
        FileUtils.deleteDirectory(new File(FileRoot));

    }


    private void downloadVideo(String text, SendChatAction sendChatAction, String chatId) throws Exception {
        sendChatAction.setAction(ActionType.RECORDVIDEONOTE);
        HtmlPage htmlPage = UnitPage(text);
        String soap = htmlPage.asXml();
        DomNode domNode = htmlPage.querySelector(".login_register_header");
        String videoName = domNode.getTextContent();
        videoName = videoName.replaceAll("\\s*", "");
        String rgex0 = "strencode2\\((.*?)\\)\\)";
        String substring1 = DealStrSub.getSubUtilSimple(soap, rgex0);
        //调用js解码地址
        String strencode = JsUtil.strencode(substring1);
        String rgex = "source src='(.*?)\\?st";
        String realVideoUrl = DealStrSub.getSubUtilSimple(strencode, rgex);

        String realPath="";
        if (env.equals("dev")){
            realPath = FILE_ROOT + videoName + "\\";
        }else {
            realPath = FILE_ROOT + videoName + "/";
        }

        File saveDir = new File(FILE_ROOT + videoName);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        //真实地址
        log.warn("真实地址：" + realVideoUrl);
        if (StringUtil.isNotBlank(realVideoUrl)){
            senTextMsg(chatId, "正在下载,请等待下载完成...., 视频真实地址：" + realVideoUrl);
        }

        //转mp4
        VideoUtils.convertToMp42(realVideoUrl,
                new File(realPath + videoName + VIDEO_JPEG),
                realPath,videoName);

        bot.sendVideo2(realPath + videoName + VIDEO_JPEG,realPath,
                " ", chatId);
        log.info("已回复");
    }

    private void senTextMsg(String chatId, String Msg) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId, Msg);
        execute(sendMessage);
    }

}
