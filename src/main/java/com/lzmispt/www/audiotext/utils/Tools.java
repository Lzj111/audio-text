package com.lzmispt.www.audiotext.utils;

import com.lzmispt.www.audiotext.entity.AudioMode;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.FileDialog;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

/**
 * @Classname Tools
 * @Description
 * @Date 2023/2/24 8:58
 * @Author by lzj
 */
public class Tools {
    private static final int MS_TO_DAYS = 86400000;
    private static final int MS_TO_HOURS = 3600000;
    private static final int MS_TO_MINUTES = 60000;
    private static final int MS_TO_SECONDS = 1000;

    /**
     * 获取音频文件
     * @return
     */
    public static AudioMode getAudioMode() {
        // 1> 打开选择框选择要修改的ppt
        FileDialog dialog = new FileDialog(new Frame(), "选择存放位置", FileDialog.LOAD);
        dialog.setFile("*.mp3;*.wav;*.wma");
        dialog.setVisible(true);
        String filePath = dialog.getDirectory() + dialog.getFile();

        System.out.println("> 用户选择的文件路径:" + filePath);
        if (filePath.contains("null")) {
            throw new RuntimeException("未选择文件");
        }

        int lastPoint = filePath.lastIndexOf(".");
        String ext = filePath.substring(lastPoint + 1);

        AudioMode audioMode = new AudioMode();
        audioMode.setFilePath(filePath);
        audioMode.setExt(ext);
        audioMode.setFileName(dialog.getFile());
        return audioMode;
    }

    /**
     * 获取音频文件
     * @return
     */
    public static String getFilePath() {
        AudioMode audioMode = getAudioMode();
        return audioMode.getFilePath();
    }

    /**
     * 获取文件流
     * @param filePath
     * @return
     */
    public static InputStream getAudioInputStream(String filePath) {
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取pcm格式的音频流
     * @param filePath
     * @return
     */
    public static AudioInputStream getPcmAudioInputStreamForMp3(String filePath) {
        File mp3 = new File(filePath);
        AudioInputStream audioInputStream = null;
        AudioFormat targetFormat = null;

        try {
            AudioInputStream in = null;
            //读取音频文件的类
            MpegAudioFileReader mp = new MpegAudioFileReader();
            in = mp.getAudioInputStream(mp3);
            AudioFormat baseFormat = in.getFormat();
            //设定输出格式为pcm格式的音频文件
            targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
                    baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
            //输出到音频
            audioInputStream = AudioSystem.getAudioInputStream(targetFormat, in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return audioInputStream;
    }

    /**
     * 将音频流转换为字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] getBytesForAudioInputStream(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        try {
            while (-1 != (n = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    /**
     * 将MP3文件转换为pcm的直接数组
     * @param filePath 文件路径
     * @return byte[]
     */
    public static byte[] getPcmBytesForAudioInputStream(String filePath) {
        AudioInputStream audioInputStream = getPcmAudioInputStreamForMp3(filePath);
        byte[] bytes = getBytesForAudioInputStream(audioInputStream);
        return bytes;
    }

    /**
     * 获取base64
     * @param filePath 文件路径
     * @return String
     */
    public static String getBase64Str(String filePath) {
        InputStream inputStream = getAudioInputStream(filePath);
        assert inputStream != null;
        byte[] bytes = getBytesForAudioInputStream(inputStream);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * info日志(蓝色)
     * @param msg
     */
    public static void soutInfo(String msg) {
        System.out.println("\033[0;34m" + msg + "\u001B[0m");
    }

    /**
     * error日志(红色)
     * @param msg
     */
    public static void soutError(String msg) {
        System.out.println("\033[0;31m" + msg + "\u001B[0m");
    }

    /**
     * 毫秒格式化
     *
     * @param mss 毫秒
     * @return java.lang.String
     * @author wlx
     * @date 2021-07-12 14:08
     */
    public static String formatDuring(long mss) {
        long days = mss / MS_TO_DAYS;
        long hours = (mss % MS_TO_DAYS) / MS_TO_HOURS;
        long minutes = (mss % MS_TO_HOURS) / MS_TO_MINUTES;
        long seconds = (mss % MS_TO_MINUTES) / MS_TO_SECONDS;
        return days + " 天 " + hours + " 小时 " + minutes + " 分钟 "
                + seconds + " 秒 ";
    }

    /**
     * 耗时计算
     * @param start
     * @param end
     */
    public static void reckonTime(long start, long end) {
        long cost = end - start;
        String costStr = cost + "毫秒";
//        if (cost < MS_TO_SECONDS) {
//            costStr = cost + "毫秒";
//        } else {
//            costStr = formatDuring(cost);
//        }
        soutInfo("语音转写耗时=>" + costStr);
    }
}

