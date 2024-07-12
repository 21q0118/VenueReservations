package com.example.reservedassistance.utils;

import com.example.reservedassistance.config.FileConfig;

import javax.annotation.Resource;
import java.io.File;

public class FileUtils {

    public static final String PREFIX = "http://127.0.0.1:8888/abc/static/";

    public static final String BASEPATH = "E:\\ReservedAssistanceFile"  ;

    public static String convertURLToPath(String imageURL){
        FileConfig fileConfig = new FileConfig();
        fileConfig.setBasePath(BASEPATH);
        String[] split = imageURL.split("/");
        String fileName = split[split.length - 1];
        return fileConfig.getBasePath() + File.separator + fileName;
    }

    public static String convertPathToURL(String imagePath){
        FileConfig fileConfig = new FileConfig();
        fileConfig.setPrefix(PREFIX);
        String[] split = imagePath.split("\\\\");
        String fileName = split[split.length - 1];
        return fileConfig.getPrefix() + fileName;

    }
}
