package com.lzmispt.www.audiotext.parse.impl;

import com.lzmispt.www.audiotext.entity.AudioMode;
import com.lzmispt.www.audiotext.utils.Tools;
import com.tencentcloudapi.asr.v20190614.AsrClient;
import com.tencentcloudapi.asr.v20190614.models.SentenceRecognitionRequest;
import com.tencentcloudapi.asr.v20190614.models.SentenceRecognitionResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.cvm.v20170312.models.DescribeRegionsResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Classname ConvertAudioTencentImpl
 * @Description
 * @Date 2023/2/24 11:18
 * @Author by lzj
 */
@Service(value = "convertAudioTencentImpl")
public class ConvertAudioTencentImpl extends ConvertAudioImpl {

    @Value("${platform-info.tencent.app-id}")
    private String appId;
    @Value("${platform-info.tencent.secret-id}")
    private String secretId;
    @Value("${platform-info.tencent.secret-key}")
    private String secretKey;
    @Value("${platform-info.tencent.endpoint}")
    private String endpoint;

    private static AsrClient clientInstance;

    /**
     * 获取腾讯客户端实例
     * @return
     */
    private AsrClient getClientInstance() {
        if (null != clientInstance) {
            return clientInstance;
        }

        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
        // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
        Credential cred = new Credential(secretId, secretKey);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(endpoint);
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        AsrClient client = new AsrClient(cred, "", clientProfile);

        clientInstance = client;
        return client;
    }

    /**
     * 生成请求数据
     * @param audioMode
     * @return
     */
    private Map<String, Object> builderPostData(AudioMode audioMode) {
        String base64Str = Tools.getBase64Str(audioMode.getFilePath());
        Map<String, Object> params = new HashMap<>(16);
        params.put("UsrAudioKey", "test");
        params.put("ProjectId", 0);
        params.put("SubServiceType", 2);
        params.put("Data", base64Str);
        params.put("EngSerViceType", "8k_zh");
        params.put("VoiceFormat", "wav");
        params.put("SourceType", 1);

        return params;
    }

    /**
     * 支持wav、mp3、m4a、flv、mp4、wma、3gp、amr、aac、ogg-opus、flac格式。
     * @param audioMode
     * @return
     */
    @Override
    public String getTextForAudio(AudioMode audioMode) {
        AsrClient client = getClientInstance();
        return getInWord(client, audioMode);
    }

    /**
     * 一句话识别
     * @param client
     * @param audioMode
     * @return
     */
    private String getInWord(AsrClient client, AudioMode audioMode) {
        // 实例化一个请求对象,每个接口都会对应一个request对象
        SentenceRecognitionRequest req = new SentenceRecognitionRequest();
        req.setProjectId(1000L);
        req.setSubServiceType(2L);
        req.setEngSerViceType("8k_zh");
        req.setSourceType(1L);
        req.setUsrAudioKey(audioMode.getFileName());
        String base64Str = Tools.getBase64Str(audioMode.getFilePath());
        req.setData(base64Str);
        req.setVoiceFormat(audioMode.getExt());

        // 返回的resp是一个DescribeRegionsResponse的实例，与请求对象对应
        try {
            // 返回的resp是一个SentenceRecognitionResponse的实例，与请求对象对应
            SentenceRecognitionResponse resp = client.SentenceRecognition(req);
            // 输出json格式的字符串回包
            return DescribeRegionsResponse.toJsonString(resp);
        } catch (TencentCloudSDKException e) {
            Tools.soutError("ConvertAudioTencentImpl.getInWord异常：" + e);
        }
        return null;
    }

}
