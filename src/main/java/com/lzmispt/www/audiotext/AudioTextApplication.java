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

        // 1> 执行
        System.out.println("****************MP3转文字******************");
        System.out.println("******* 请输入编码：0[百度]，1[腾讯]，2[Vosk开源] ");
        Scanner sca = new Scanner(System.in);
        int type = sca.nextInt();
        String beanName = "convertAudioBaiduImpl";
        switch (type) {
            case 1:
                beanName = "convertAudioTencentImpl";
                break;
            case 0:
                beanName = "convertAudioBaiduImpl";
                break;
            case 2:
                beanName = "convertAudioVoskImpl";
                break;
        }

        IConvertAudio convertAudio = BeanUtil.getInstanceByBeanName(beanName);
        if (null == convertAudio) {
            return;
        }
        AudioMode audioMode = Tools.getAudioMode();
        String text = convertAudio.getTextForAudio(audioMode);
        String printStr = "解析的文字内容是=>" + text;
        Tools.soutInfo(printStr);

        // 执行完成后退出程序
        System.exit(0);
    }

}
