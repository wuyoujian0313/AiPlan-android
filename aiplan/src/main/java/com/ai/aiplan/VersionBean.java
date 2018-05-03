package com.ai.aiplan;

/**
 * Created by wuyoujian on 2018/5/3.
 */

public class VersionBean {
    private String platform;
    private String versionNumber;

    public String getVersionURL() {
        return versionURL;
    }

    public void setVersionURL(String versionURL) {
        this.versionURL = versionURL;
    }

    private String versionURL;


    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }
}
