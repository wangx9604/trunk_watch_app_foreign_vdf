package com.mi.mimsgsdk.utils;

import android.os.Environment;

import java.io.File;

public class MsgSdkUtils {

    public static File getAgorasdkLogFile() {
        File baseDir;
        baseDir = new File("MiMsg");
        return new File(baseDir, "agorasdk.log");
    }
}
