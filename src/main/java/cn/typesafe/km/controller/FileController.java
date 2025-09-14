package cn.typesafe.km.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/files")
public class FileController {

    // 文件存储目录，修改为适用于Docker部署的固定路径
    private static final String UPLOAD_DIR = "/app/uploads/";

    public FileController() {
        // 确保上传目录存在
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    /**
     * 上传文件并返回相对路径
     *
     * @param file 上传的文件
     * @return 文件的相对路径
     * @throws IOException 文件保存异常
     */
    @PostMapping("/upload")
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // 保存文件
        Path filePath = Paths.get(UPLOAD_DIR, uniqueFileName);
        Files.write(filePath, file.getBytes());

        // 返回相对路径
        String relativePath = UPLOAD_DIR + uniqueFileName;
        return Map.of("path", relativePath);
    }
}