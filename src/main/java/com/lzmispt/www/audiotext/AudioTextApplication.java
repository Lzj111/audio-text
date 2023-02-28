package com.lzmispt.www.audiotext;

import com.lzmispt.www.audiotext.entity.AudioMode;
import com.lzmispt.www.audiotext.parse.IConvertAudio;
import com.lzmispt.www.audiotext.utils.BeanUtil;
import com.lzmispt.www.audiotext.utils.Tools;
import java.util.Scanner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class AudioTextApplication {

    public static void main(String[] args) {
//        SpringApplication.run(AudioTextApplication.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(AudioTextApplication.class);
        builder.headless(false).web(WebApplicationType.SERVLET).run(args);

        Boolean isContinue = true;
        while (isContinue) {
            isContinue = exec();
        }

        // 执行完成后退出程序
        System.exit(0);
    }

    public static Boolean exec() {
        // 1> 执行
        System.out.println("****************MP3转文字******************");
        System.out.println("******* 请输入编码：0[百度]，1[腾讯]，2[Vosk开源]，3[讯飞]，4[阿里] 其他退出... ");
        Scanner sca = new Scanner(System.in);
        int type = sca.nextInt();
        String beanName = null;
        switch (type) {
            case 1: //
                beanName = "convertAudioTencentImpl";
                break;
            case 0: // 百度
                beanName = "convertAudioBaiduImpl";
                break;
            case 2: // VOSK
                beanName = "convertAudioVoskImpl";
                break;
            case 3: // 讯飞
                beanName = "convertAudioXunFeiImpl";
                break;
            case 4: // 阿里
                beanName = "convertAudioAliImpl";
                break;
            default:
                break;
        }

        if (null == beanName) {
            return false;
        }
        IConvertAudio convertAudio = BeanUtil.getInstanceByBeanName(beanName);
        if (null == convertAudio) {
            return false;
        }
        AudioMode audioMode = Tools.getAudioMode();
        // 记录开始时间
        long start = System.currentTimeMillis();
        String text = convertAudio.getTextForAudio(audioMode);
        // 记录结束时间
        long end = System.currentTimeMillis();
        Tools.soutError("=======================================");
        Tools.soutInfo("语音转写内容=>" + text);
        Tools.reckonTime(start, end);
        return true;
    }
}
