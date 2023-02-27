package com.lzmispt.www.audiotext.parse.impl;

import com.baidu.aip.speech.AipSpeech;
import com.lzmispt.www.audiotext.entity.AudioMode;
import com.lzmispt.www.audiotext.parse.IConvertAudio;
import com.lzmispt.www.audiotext.utils.Tools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Classname GetAuthorityImpl
 * @Description
 * @Date 2023/2/23 17:12
 * @Author by lzj
 */
@Service(value = "convertAudioBaiduImpl")
public class ConvertAudioBaiduImpl extends ConvertAudioImpl {

    @Value("${platform-info.baidu.app-id}")
    private String appId;
    @Value("${platform-info.baidu.api-key}")
    private String apiKey;
    @Value("${platform-info.baidu.secret-key}")
    private String secretKey;

    private static AipSpeech authorityClient;

    /**
     * 获取
     * @param appId
     * @param apiKey
     * @param secretKey
     * @return
     */
    private AipSpeech getAuthorityClient(String appId, String apiKey, String secretKey) {
        if (null != authorityClient) {
            return authorityClient;
        }

        // 初始化一个AipSpeech
        authorityClient = new AipSpeech(appId, apiKey, secretKey);

        // 可选：设置网络连接参数
        authorityClient.setConnectionTimeoutInMillis(2000);
        authorityClient.setSocketTimeoutInMillis(60000);

//        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
//
//        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
//        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
//
//        // 调用接口
//        JSONObject res = client.asr("test.pcm", "pcm", 16000, null);
//        System.out.println(res.toString(2));
        return authorityClient;
    }

    /**
     * 是否支持的格式
     * @param ext
     * @return
     */
    private Boolean isSupportedExt(String ext) {
        List<String> listExt = new ArrayList<String>(
                Arrays.asList("pcm", "wav", "arm"));
        return listExt.contains(ext);
    }

    /**
     * 目前格式仅仅支持 pcm，wav 或 amr，如填写 mp3 即会有此错误
     * @param audioMode
     * @return
     */
    @Override
    public String getTextForAudio(AudioMode audioMode) {
        AipSpeech client = getAuthorityClient(appId, apiKey, secretKey);
        // 调用接口
        String filePath = audioMode.getFilePath();
        String ext = audioMode.getExt();
        JSONObject res;
        if (isSupportedExt(ext)) {
            res = client.asr(filePath, ext, 16000, null);
        } else {
            String extPcm = "pcm";
            byte[] bytes = Tools.getPcmBytesForAudioInputStream(filePath);
            res = client.asr(bytes, extPcm, 16000, null);
        }
        return res.toString();
    }

}
