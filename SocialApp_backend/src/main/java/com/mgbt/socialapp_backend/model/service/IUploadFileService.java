package com.mgbt.socialapp_backend.model.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface IUploadFileService {
    public Resource charge(String fileName) throws MalformedURLException;

    public String save(MultipartFile file) throws IOException;

    public Boolean delete(String fileName);

    public Path getPath(String fileName);
}
