package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.exceptions.FileNameTooLongException;
import org.springframework.core.io.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
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
        } else if (!resource.exists() && !resource.isReadable() && (finalDirectory.contains("posts") || (finalDirectory.contains("messages")))) {
            filePath = Paths.get("src/main/resources/static/images").resolve("no-image.jpg").toAbsolutePath();
            resource = new UrlResource(filePath.toUri());
        }
        return resource;
    }

    @Override
    public String save(MultipartFile file, String finalDirectory) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replace(" ", "");
        /*
        The application creates a random UUID of 37 characters which is added to the image name
        (and its extension) so that, rounding up, only a 60 character name can be placed as the
        database supports 100 characters.
        */
        if (fileName.length() > 100) {
            throw new FileNameTooLongException("The file name is too long (max 60 characters)");
        } else {
            Path filePath = getPath(fileName, finalDirectory);
            Files.copy(file.getInputStream(), filePath);
            return fileName;
        }
    }

    @Override
    public Boolean delete(String fileName, String directory) {
        if(fileName != null && fileName.length() > 0) {
            Path lastFilePath = Paths.get("uploads" + directory).resolve(fileName).toAbsolutePath();
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
