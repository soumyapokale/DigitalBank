package com.bank.DigitalBank.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonUtil {

    public static final ObjectMapper mapper = new ObjectMapper();



    public static <T> T readJsonFileAsObject(String filePath, Class<T> clazz) throws IOException {
        return mapper.readValue(new File("src/test/resources/Testing/User/" + filePath), clazz);
    }

    public static String readJsonFileAsString(String filePath) throws IOException {
        return Files.readString(Paths.get("src/test/resources/Testing/User" + filePath));
    }

}
