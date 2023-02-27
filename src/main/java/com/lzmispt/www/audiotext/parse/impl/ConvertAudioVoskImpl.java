package com.lzmispt.www.audiotext.parse.impl;

import com.lzmispt.www.audiotext.entity.AudioMode;
import com.lzmispt.www.audiotext.utils.Tools;
import com.lzmispt.www.audiotext.utils.voiceutil.VoiceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Classname ConvertAudioVoskImpl
 * @Description
 * @Date 2023/2/24 16:26
 * @Author by lzj
 */
@Service("convertAudioVoskImpl")
public class ConvertAudioVoskImpl extends ConvertAudioImpl {

    @Value("${leenleda.vosk.model}")
    private String VOSKMODELPATH;

    @Override
    public String getTextForAudio(AudioMode audioMode) {
        try {
            String text = VoiceUtil.getWord(audioMode.getFilePath(), VOSKMODELPATH);
            return text;
        } catch (Exception e) {
            Tools.soutError("ConvertAudioTencentImpl.getInWord异常：" + e);
        }
        return null;
    }
}
