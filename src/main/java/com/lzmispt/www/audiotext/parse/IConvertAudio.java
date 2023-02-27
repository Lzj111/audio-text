package com.lzmispt.www.audiotext.parse;

import com.lzmispt.www.audiotext.entity.AudioMode;

/**
 * @Classname IGetAuthority
 * @Description
 * @Date 2023/2/23 17:08
 * @Author by lzj
 */
public interface IConvertAudio {

    /**
     * 获取音频的文本信息
     * @param audioMode
     * @return
     */
    String getTextForAudio(AudioMode audioMode);
}
