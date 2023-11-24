package ru.hbb.Utills;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import ru.hbb.ConfigDataContainers.CryptData;
import ru.hbb.ConfigDataContainers.NetData;
import ru.hbb.ConfigDataContainers.UserInfo;
import ru.hbb.Console.Logger.SimpleLogger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileUtils {

    private final File file;

    public FileUtils(String path) {
        this.file = new File(path);
    }

    public Object read() {
        FileType fileType = FileType.getFileType(file);

        if (fileType == null) {
            return null;
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            }catch (IOException e) {

            }
        }

        switch (fileType) {
            case JSON:
                return readJSON();
            default:
                return null;
        }
    }

    public void write(Object o) {
        FileType fileType = FileType.getFileType(file);

        if (fileType == null) {
            return;
        }

        switch (fileType) {
            case JSON:
                writeJSON(o);
        }
    }

    private void writeJSON(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, obj);
        }catch (Exception e) {
            e.printStackTrace();
            SimpleLogger.error("File {name} error to write".replace("{name}", file.getName()));
        }
    }

    private Object readJSON() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (file.getName().startsWith(FileName.NET_CONFIG.getSimpleName())) {
                return objectMapper.readValue(file, new TypeReference<List<NetData>>() {
                });
            }
            if (file.getName().startsWith(FileName.CRYPT_CONFIG.getSimpleName())) {
                return objectMapper.readValue(file, CryptData.class);
            }
            if (file.getName().startsWith(FileName.USER_DATA.getSimpleName())) {
                return objectMapper.readValue(file, new TypeReference<List<UserInfo>>() {
                });
            }
        } catch (MismatchedInputException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            SimpleLogger.error("File {name} error to read".replace("{name}", file.getName()));
            return null;
        }
        return null;
    }
}
