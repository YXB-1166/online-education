package com.edu.exam.controller;

import com.edu.common.annotation.RequireRole;
import com.edu.common.result.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("submission")
public class UploadController {

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @PostMapping("/upload")
    @RequireRole(1)
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();
        String ext = "";
        String name = file.getOriginalFilename();
        if (name != null && name.contains(".")) {
            ext = name.substring(name.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString() + ext;
        Path target = Paths.get(uploadPath, storedName);
        Files.copy(file.getInputStream(), target);
        String url = "/submission/file/" + storedName;
        return Result.ok(Map.of("url", url, "originalName", name));
    }

    @GetMapping("/file/{filename}")
    @RequireRole({1, 2})
    public ResponseEntity<Resource> download(@PathVariable String filename, HttpServletResponse response) throws IOException {
        Path file = Paths.get(uploadPath, filename);
        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Files.probeContentType(file);
        if (contentType == null) contentType = "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header("Content-Disposition", "inline; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\"")
                .body(resource);
    }
}
