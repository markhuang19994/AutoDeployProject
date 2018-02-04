package com.xxxcomp.tw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>2018/2/2, MarkHuang,new
 * </ul>
 * @since 2018/2/2
 */
public class FileUtil {
    public static File searchFileInDirectory(File file, String fileName) {
        List<File> fileList = new ArrayList<>();
        fileList.add(file);
        return searchFileInDirectory(fileList, fileName);
    }

    private static File searchFileInDirectory(List<File> fileList, String fileName) {
        List<File> tempFileList = new ArrayList<>();
        boolean lastFile = true;
        for (File outerFile : fileList) {
            File[] innerFiles = outerFile.listFiles();
            if (innerFiles == null) continue;
            for (File innerFile : innerFiles) {
                if (innerFile.isFile()) {
                    if (!innerFile.getAbsolutePath().contains("target") && fileName.equals(innerFile.getName())) {
                        return innerFile;
                    }
                } else if (innerFile.isDirectory()) {
                    File[] inInnerFiles = innerFile.listFiles();
                    if (inInnerFiles != null && inInnerFiles.length != 0) lastFile = false;
                    tempFileList.add(innerFile);
                }
            }
        }
        fileList = tempFileList;
        if (lastFile) return null;
        return searchFileInDirectory(fileList, fileName);
    }

    static void makeDirFormProperties(String filePath) {

        Properties p = new Properties();
        try {
            p.load(new FileInputStream(filePath));
            p.forEach((k, v) -> {
                if (String.valueOf(v).contains("C:/") && !String.valueOf(v).contains(".")) {
                    File f = new File(String.valueOf(v));
                    if (!f.exists() && f.mkdirs())
                        System.out.println("Create dir " + k + " !");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
