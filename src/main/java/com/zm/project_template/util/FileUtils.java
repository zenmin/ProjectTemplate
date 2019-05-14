package com.zm.project_template.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.UUID;

/**
 * @author TangFD@HF 2018/5/29
 */
public class FileUtils {
    public static void createDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String dealTargetDir(String targetDir) {
        targetDir = targetDir == null ? "" : targetDir;
        if (StringUtils.isNotEmpty(targetDir)) {
            createDir(targetDir);
        }

        if (!targetDir.endsWith(File.separator)) {
            targetDir += File.separator;
        }
        return targetDir;
    }

    public static File checkFileExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("file not exists ![filepath = " + filePath + "]");
        }

        return file;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static void writeStringToFile(File file, String content, String s) {

        try {
            InputStream inputStream = new ByteArrayInputStream(content.getBytes(s));
            OutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断指定路径的文件是否存在
     * @param path
     * @return
     */
    public static boolean fileExists(String path) {
        if(path == null || path.length() == 0)
            return false;
        File f = new File(path);
        if(f.exists())
            return true;
        return false;
    }

    /**
     * 删除文件或文件件
     * @param filePath
     */
    public static void  deleteFileOrDir(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if(file.isFile()){
                    file.delete();
                }else if(file.isDirectory()){
                    deleteFolder(filePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件夹 param folderPath 文件夹完整绝对路径
     */
    public static void deleteFolder(String folderPath) {
        try {
            deleteAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下所有文件 param path 文件夹完整绝对路径
     *
     * @param path
     * @return
     */
    public static boolean deleteAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                deleteAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                deleteFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }
}
