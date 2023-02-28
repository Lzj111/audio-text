package com.lzmispt.www.audiotext.parse.impl;

import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.InputFormatEnum;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.asr.SpeechRecognizer;
import com.alibaba.nls.client.protocol.asr.SpeechRecognizerListener;
import com.alibaba.nls.client.protocol.asr.SpeechRecognizerResponse;
import com.lzmispt.www.audiotext.entity.AudioMode;
import com.lzmispt.www.audiotext.utils.Tools;
import java.io.File;
import java.io.FileInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Classname ConvertAudioAliImpl
 * @Description
 * @Date 2023/2/27 17:34
 * @Author by lzj
 */
@Service("convertAudioAliImpl")
public class ConvertAudioAliImpl extends ConvertAudioImpl {

    @Value("${platform-info.aliyun.app-key}")
    private String appkey = "";
    @Value("${platform-info.aliyun.access-key-id}")
    private String accessKeyId = "";
    @Value("${platform-info.aliyun.access-key-secret}")
    private String accessKeySecret = "";
    // 默认值：wss://nls-gateway.cn-shanghai.aliyuncs.com/ws/v1
    private String url = null;
    // 采样率
    private Integer sampleRate = 16000;

    /**
     * 语音处理客户端
     */
    private static NlsClient client;

    /**
     * 初始化client对象
     */
    public void initNlsClient() {
        // 应用全局创建一个NlsClient实例，默认服务地址为阿里云线上服务地址。
        // 获取Token，实际使用时注意在accessToken.getExpireTime()过期前再次获取。
        AccessToken accessToken = new AccessToken(accessKeyId, accessKeySecret);
        try {
            accessToken.apply();
            Tools.soutInfo("ConvertAudioAliImpl.initNlsClient=>token:" +
                    accessToken.getToken() + ",expre:" + accessToken.getExpireTime());
            if (null == url || url.isEmpty()) {
                client = new NlsClient(accessToken.getToken());
            } else {
                client = new NlsClient(url, accessToken.getToken());
            }
        } catch (Exception e) {
            Tools.soutError("ConvertAudioAliImpl.initNlsClient=>" + e);
        }
    }

    /**
     * 根据二进制数据大小计算对应的同等语音长度 sampleRate仅支持8000或16000。
     * @param dataSize
     * @param sampleRate
     * @return
     */
    private int getSleepDelta(int dataSize, int sampleRate) {
        // 仅支持16位采样。
        int sampleBytes = 16;
        // 仅支持单通道。
        int soundChannel = 1;
        return (dataSize * 10 * 8000) / (160 * sampleRate);
    }

    /**
     * 关闭连接
     */
    private void shutdown() {
        client.shutdown();
    }

    private String rtnStr = null;

    private SpeechRecognizerListener getRecognizerListener(int myOrder, String userParam) {
        SpeechRecognizerListener listener = new SpeechRecognizerListener() {
            // 识别出中间结果。仅当setEnableIntermediateResult为true时，才会返回该消息。
            @Override
            public void onRecognitionResultChanged(SpeechRecognizerResponse response) {
                // getName是获取事件名称，getStatus是获取状态码，getRecognizedText是语音识别文本。
                Tools.soutInfo("ConvertAudioAliImpl.getRecognizerListener.onRecognitionResultChanged=>name: "
                        + response.getName() + ", status: " + response.getStatus() + ", result: " +
                        response.getRecognizedText());
            }

            //识别完毕
            @Override
            public void onRecognitionCompleted(SpeechRecognizerResponse response) {
                //getName是获取事件名称，getStatus是获取状态码，getRecognizedText是语音识别文本。
                String str = "name: " + response.getName() + ", status: " + response.getStatus() + ", result: " +
                        response.getRecognizedText();
                Tools.soutInfo("ConvertAudioAliImpl.getRecognizerListener.onRecognitionCompleted=>" + str);
                rtnStr = str;
            }

            @Override
            public void onStarted(SpeechRecognizerResponse response) {
                Tools.soutInfo("ConvertAudioAliImpl.getRecognizerListener.onStarted=>myOrder: "
                        + myOrder + "; myParam: " + userParam + "; task_id: " + response.getTaskId());
            }

            @Override
            public void onFail(SpeechRecognizerResponse response) {
                //task_id是调用方和服务端通信的唯一标识，当遇到问题时，需要提供此task_id。
                Tools.soutError("ConvertAudioAliImpl.getRecognizerListener.onFail=>task_id: "
                        + response.getTaskId() + ", status: " + response.getStatus() + ", status_text: " +
                        response.getStatusText());
            }
        };
        return listener;
    }

    /**
     * 阿里 支持单轨和双轨的WAV、MP3、M4A、WMA、ACC、OGG、AMR、FLAC格式录音文件识别音频文件大小不超过512 MB，视频文件大小不超过2 GB)
     * @param audioMode
     * @return
     */
    @Override
    public String getTextForAudio(AudioMode audioMode) {
        initNlsClient();
        SpeechRecognizer recognizer = null;
        try {
            // 传递用户自定义参数
            String myParam = "user-param";
            int myOrder = 1234;
            SpeechRecognizerListener listener = getRecognizerListener(myOrder, myParam);
            recognizer = new SpeechRecognizer(client, listener);
            recognizer.setAppKey(appkey);
            // 设置音频编码格式。如果是OPUS文件，请设置为InputFormatEnum.OPUS。
            if (audioMode.getExt().contains("wav")) {
                recognizer.setFormat(InputFormatEnum.WAV);
            }

            // 设置音频采样率
            if (sampleRate == 16000) {
                recognizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            } else if (sampleRate == 8000) {
                recognizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_8K);
            }
            // 设置是否返回中间识别结果
            recognizer.setEnableIntermediateResult(true);
            // 设置是否打开语音检测（即vad）
            recognizer.addCustomedParam("enable_voice_detection", true);
            // 此方法将以上参数设置序列化为JSON发送给服务端，并等待服务端确认。
            long now = System.currentTimeMillis();
            recognizer.start();

            Tools.soutInfo("ConvertAudioAliImpl.getTextForAudio=>ASR start latency : " + (System.currentTimeMillis() - now) + " ms");
            File file = new File(audioMode.getFilePath());
            FileInputStream fis = new FileInputStream(file);
            byte[] b = new byte[3200];
            int len;
            while ((len = fis.read(b)) > 0) {
                recognizer.send(b, len);
                //本案例用读取本地文件的形式模拟实时获取语音流，因为读取速度较快，这里需要设置sleep时长。
                // 如果实时获取语音则无需设置sleep时长，如果是8k采样率语音第二个参数设置为8000。
                int deltaSleep = getSleepDelta(len, sampleRate);
                Thread.sleep(deltaSleep);
            }
            // 通知服务端语音数据发送完毕，等待服务端处理完成。
            now = System.currentTimeMillis();
            // 计算实际延迟，调用stop返回之后一般即是识别结果返回时间。
            Tools.soutInfo("ConvertAudioAliImpl.getTextForAudio=>ASR wait for complete");
            recognizer.stop();
            Tools.soutInfo("ConvertAudioAliImpl.getTextForAudio=>ASR stop latency : " + (System.currentTimeMillis() - now) + " ms");
            fis.close();
        } catch (Exception e) {
            Tools.soutError("ConvertAudioAliImpl.getTextForAudio=>" + e);
        } finally {
            //关闭连接
            if (null != recognizer) {
                recognizer.close();
            }
            shutdown();
        }
        return rtnStr;
    }
}
