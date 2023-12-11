package org.contesthub.cdn.services;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageServiceImpl {
    private final Path rootLocation;

    @Autowired
    public StorageServiceImpl(StorageProperties properties) {
        assert properties.getLocation() != null && !properties.getLocation().isBlank();
        this.rootLocation = Paths.get(properties.getLocation());

        this.init();
    }

    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public Path store(MultipartFile file, String directory) throws FileUploadException {
        Path destinationFile;
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file " + file.getOriginalFilename());
            }
            assert file.getOriginalFilename() != null;
            destinationFile = this.rootLocation.resolve(
                            Paths.get(directory, file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().startsWith(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException(
                        "Cannot store file outside current directory " + file.getOriginalFilename());
            }
            Files.createDirectories(destinationFile.getParent());
            file.transferTo(destinationFile.toFile());
        } catch (RuntimeException | IOException e) {
            throw new FileUploadException("Failed to store file " + file.getOriginalFilename(), e);
        }
        return rootLocation.toAbsolutePath().relativize(destinationFile);
    }

    public List<?> loadAll() throws FileNotFoundException{
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> {
                        if (Files.isDirectory(path)) {
                            return this.loadFromSubDir(path.getFileName().toString());
                        } else {
                            return rootLocation.relativize(path);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new FileNotFoundException("Failed to read stored files");
        }
    }

    public List<?> loadFromSubDir(String directory) {
        try {
            return Files.walk(this.rootLocation.resolve(directory), 1)
                    .filter(path -> !path.equals(this.rootLocation.resolve(directory)))
                    .map(path -> {
                        if (Files.isDirectory(path)) {
                            return this.loadFromSubDir(path.getFileName().toString());
                        } else {
                            return rootLocation.relativize(path);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stored files");
        }
    }

    public Path load(String filename) throws FileNotFoundException{
        Path file = rootLocation.resolve(filename);
        if (Files.exists(file)) {
            return file;
        } else {
            throw new FileNotFoundException("File not found " + filename);
        }
    }

    public Resource loadAsResource(String filename) throws FileNotFoundException {
        Path file = this.load(filename);
        try {
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename);
        }
    }

    public void delete(String filename) throws FileNotFoundException {
        try {
            Files.delete(this.load(filename));
        } catch (IOException e) {
            throw new FileNotFoundException("Failed to delete file " + filename);
        }
    }

    public void deleteAll() throws FileSystemException {
        try {
            Files.walk(this.rootLocation)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
        } catch (IOException e) {
            throw new FileSystemException("Failed to delete all files");
        }
    }
}
