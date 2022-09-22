package com.mgbt.socialapp_backend.model.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface IUploadFileService {
    Resource charge(String fileName, String finalDirectory) throws MalformedURLException;

    String save(MultipartFile file, String finalDirectory) throws IOException;

    Boolean delete(String fileName, String directory);

    Path getPath(String fileName, String finalDirectory);
}
