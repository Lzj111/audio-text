package com.lzmispt.www.audiotext.entity;

/**
 * @Classname AudioMode
 * @Description
 * @Date 2023/2/24 9:57
 * @Author by lzj
 */
public class AudioMode {

    private String filePath;
    private String ext;
    private String fileName;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
