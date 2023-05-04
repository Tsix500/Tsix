package com.troublefixer.utils;

import com.troublefixer.pojo.History;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class FileUtils {

    private FileUtils(){}

    private final static String modelPath = "/resources/trainmodels";

    private final static String prefix = "model";

    public static Boolean checkType(String fileType, String[] type){
        for(String s : type){
            if(s.equals(fileType)){
                return true;
            }
        }
        return false;
    }

    public static void saveFile(File file){
        String oldName = file.getName();
        String type = oldName.substring(oldName.lastIndexOf("."));
        String fileName = prefix +  String.valueOf(new Date().getTime()) + type;
        if(!file.exists()){
            file.mkdir();
        }
    }

    public static File saveMultiFile(MultipartFile trainfile, String path) {

        File file = new File(path);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            //获得绝对路径
            file = file.getAbsoluteFile();
            //保存文件需要绝对路径
            trainfile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
