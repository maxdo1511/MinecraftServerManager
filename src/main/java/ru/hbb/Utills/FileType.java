package ru.hbb.Utills;

import java.io.File;

public enum FileType {

    JSON,
    TXT,
    XML;

    public static FileType getFileType(File file) {
        String exp = file.getName().split("\\.")[1];
        try {
            return valueOf(exp.toUpperCase());
        }catch (IllegalArgumentException e) {
            return null;
        }
    }

}
