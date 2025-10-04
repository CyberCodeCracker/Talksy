package com.amouri_dev.talksy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtils {

    private FileUtils() {}

    public static byte[] readFileFromLocation(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return new byte[0];
        }
        try {
            Path file = new File(fileUrl).toPath();
            return Files.readAllBytes(file);
        } catch (IOException e) {
            log.warn("No file found in {}", fileUrl);
        }
        return new byte[0];
    }
}
