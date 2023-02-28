package com.lzmispt.www.audiotext.utils.xfutil.util;

import com.alibaba.fastjson.JSON;
import com.lzmispt.www.audiotext.utils.Tools;
import com.lzmispt.www.audiotext.utils.xfutil.request.OpenReq;
import lombok.Builder;
import lombok.Data;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Data
@Builder
public class OpenCaller {

    private String ulrPrefix;
    private String apiKey;
    private String apiSecret;
    private OkHttpClient client;

    public OpenResp<OpenResp.CreateData> create(OpenReq.Create req) {
        try {
            String reqUrl = ulrPrefix + "/ost/pro_create";
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), JSON.toJSONString(req));
            Authentication.AUthResult result = Authentication.auth(Authentication.AuthParam.builder()
                    .apiKey(apiKey).apiSecret(apiSecret).method(Authentication.Method.POST).reqUrl(reqUrl).build());
            Request request = new Request.Builder().url(reqUrl).
                    addHeader("Content-Type", "application/json").
                    addHeader("Date", result.getDate()).
                    addHeader("Digest", result.getDigest()).
                    addHeader("Authorization", result.getAuthorization()).
                    post(requestBody).build();
            Response resp = client.newCall(request).execute();
            if (resp.code() != 200) {
                Tools.soutError("ConvertAudioTencentImpl.OpenCaller.create=>" +
                        JSON.parseObject(resp.body().bytes(), OpenResp.class).toString());
                // throw new RuntimeException(String.format("http response is not 200(%s)", resp.code()));
            }
            return JSON.parseObject(resp.body().bytes(), OpenResp.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public OpenResp<OpenResp.QueryData> query(OpenReq.Query req) {
        try {
            String reqUrl = ulrPrefix + "/ost/query";
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), JSON.toJSONString(req));
            Authentication.AUthResult result = Authentication.auth(Authentication.AuthParam.builder()
                    .apiKey(apiKey).apiSecret(apiSecret).method(Authentication.Method.POST).reqUrl(reqUrl).build());
            Request request = new Request.Builder().url(reqUrl).
                    addHeader("Content-Type", "application/json").
                    addHeader("Date", result.getDate()).
                    addHeader("Digest", result.getDigest()).
                    addHeader("Authorization", result.getAuthorization()).
                    post(requestBody).build();
            Response resp = client.newCall(request).execute();
            if (resp.code() != 200) {
                Tools.soutError("ConvertAudioTencentImpl.OpenCaller.query=>" +
                        JSON.parseObject(resp.body().bytes(), OpenResp.class).toString());
            }
            return JSON.parseObject(resp.body().bytes(), OpenResp.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public OpenResp<OpenResp.CancelData> cancel(OpenReq.Cancel req) {
        try {
            String reqUrl = ulrPrefix + "/ost/cancel";
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), JSON.toJSONString(req));
            Authentication.AUthResult result = Authentication.auth(Authentication.AuthParam.builder()
                    .apiKey(apiKey).apiSecret(apiSecret).method(Authentication.Method.POST).reqUrl(reqUrl).build());
            Request request = new Request.Builder().url(reqUrl).
                    addHeader("Content-Type", "application/json").
                    addHeader("Date", result.getDate()).
                    addHeader("Digest", result.getDigest()).
                    addHeader("Authorization", result.getAuthorization()).
                    post(requestBody).build();
            Response resp = client.newCall(request).execute();
            System.err.println(resp);
            if (resp.code() != 200) {
                System.out.println(JSON.parseObject(resp.body().bytes(), OpenResp.class).toString());
                // throw new RuntimeException(String.format("http response is not 200(%s)", resp.code()));
            }
            return JSON.parseObject(resp.body().bytes(), OpenResp.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
