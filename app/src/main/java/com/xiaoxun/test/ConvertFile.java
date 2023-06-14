package com.xiaoxun.test;

import android.os.Environment;
import android.util.Base64;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by xilvkang on 2015/11/12.
 */
public class ConvertFile {
    private static final String DIR_PATH = Environment.getExternalStorageDirectory() + "/Imibaby/convert";


    public static void initDir(){
        File fp = new File(DIR_PATH);
        if(!fp.exists()){
            fp.mkdirs();
        }
    }

    public static void convertFiles(){
        File fp = new File(DIR_PATH);
        if(!fp.exists()){
            fp.mkdirs();
        }

        String[] fileList = fp.list();
        for(int i=0;i<fileList.length;i++){
            String[] ret = fileList[i].split(".");
            if(ret.length == 0)
                convertFile(fileList[i]);
        }
    }

    private static void convertFile(String filename){
        byte[] bitmapArray;
        JSONObject content = null;
        File fp = new File(DIR_PATH,filename);
        try {
            FileInputStream in = new FileInputStream(fp);
            int len = in.available();
            byte[] buf = new byte[len];
            in.read(buf);
            content = (JSONObject)JSONValue.parse(buf);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmapArray = Base64.decode((String) content.get("Content"), Base64.NO_WRAP);
        File voice = new File(DIR_PATH, filename + ".amr");
        if(!voice.exists()){
            try {
                voice.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(voice);
            out.write(bitmapArray);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
