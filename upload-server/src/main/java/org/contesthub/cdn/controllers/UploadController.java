package org.contesthub.cdn.controllers;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.contesthub.cdn.services.StorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.security.Principal;
import java.util.List;

@Controller()
public class UploadController {
    @Autowired
    private StorageServiceImpl storageService;

    // TODO: Authorize request against database
    @PostMapping("/upload/{problemId}")
    public ResponseEntity<?> upload(Principal principal, @PathVariable("problemId") String problemId, @RequestParam("file") MultipartFile file) throws FileUploadException {
        String username = ((JwtAuthenticationToken) principal).getTokenAttributes().get("preferred_username").toString();
        this.storageService.init();
        Path uploadedFile = this.storageService.store(file, username + "/" + problemId);
        return ResponseEntity.ok().body("Upload successful " + uploadedFile.getFileName().toString());
    }

    @GetMapping("/")
    public ResponseEntity<?> hello() throws FileNotFoundException {
        this.storageService.init();
        List<?> files = this.storageService.loadAll();
        return ResponseEntity.ok(files);
    }

    @GetMapping("/check")
    public ResponseEntity<?> check(@RequestParam String path) throws FileNotFoundException {
        this.storageService.init();
        UrlResource file = (UrlResource) this.storageService.loadAsResource(path);
        return ResponseEntity.ok(file.getURL());
    }
}
