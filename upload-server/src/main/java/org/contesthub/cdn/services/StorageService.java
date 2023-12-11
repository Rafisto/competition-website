package org.contesthub.cdn.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;
import java.nio.file.Path;
import java.util.List;

public interface StorageService {
    void init();

    void store(MultipartFile file) throws FileUploadException;

    void delete(String filename) throws FileNotFoundException;

    Path load(String filename);

    List<Path> loadAll() throws FileNotFoundException;

    Resource loadAsResource(String filename) throws FileNotFoundException;

    void deleteAll() throws FileSystemException;
}
