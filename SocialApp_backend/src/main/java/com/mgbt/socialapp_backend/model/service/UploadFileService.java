package com.mgbt.socialapp_backend.model.service;

import org.springframework.core.io.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class UploadFileService implements IUploadFileService {

    private final static String DIRECTORY_UPLOAD = "uploads";

    @Override
    public Resource charge(String fileName, String finalDirectory) throws MalformedURLException {
        Path filePath = getPath(fileName, finalDirectory);
        Resource resource = new UrlResource(filePath.toUri());
        if(!resource.exists() && !resource.isReadable() && finalDirectory.contains("users")) {
            filePath = Paths.get("src/main/resources/static/images").resolve("no-photo.jpg").toAbsolutePath();
            resource = new UrlResource(filePath.toUri());
        } else if (!resource.exists() && !resource.isReadable() && finalDirectory.contains("posts")) {
            filePath = Paths.get("src/main/resources/static/images").resolve("no-image.jpg").toAbsolutePath();
            resource = new UrlResource(filePath.toUri());
        }
        return resource;
    }

    @Override
    public String save(MultipartFile file, String finalDirectory) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replace(" ", "");
        Path filePath = getPath(fileName, finalDirectory);
        Files.copy(file.getInputStream(), filePath);
        return fileName;
    }

    @Override
    public Boolean delete(String fileName) {
        if(fileName !=null && fileName.length() >0) {
            Path lastFilePath = Paths.get("uploads").resolve(fileName).toAbsolutePath();
            File lastFile = lastFilePath.toFile();
            if(lastFile.exists() && lastFile.canRead()) {
                lastFile.delete();
                return true;
            }
        }
        return false;
    }

    @Override
    public Path getPath(String fileName, String finalDirectory) {
        return Paths.get(DIRECTORY_UPLOAD + finalDirectory).resolve(fileName).toAbsolutePath();
    }
}
