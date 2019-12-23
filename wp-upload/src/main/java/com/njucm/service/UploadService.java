package com.njucm.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UploadService {

    @Autowired
    ServletContext context;

    private final static String HOST = "http://127.0.0.1";
    private final static String PORT = "8082";
    private final static String DIR = "/upload/images";

    private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg");

    public String upload(MultipartFile file) {
        String type = file.getContentType();
        if (!suffixes.contains(type)) {
            log.info("上传失败，类型不匹配: {}", type);
            return null;
        }
        String upload = context.getRealPath(DIR);
//        String path = UploadService.class.getResource("").getPath();
//        log.info("path: {}", path);
        log.info("upload url: {}", upload);
//        log.info("url: {}", context.getContextPath());
        File dir = new File(upload);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        BufferedImage image = null;
        try {
            image = ImageIO.read(file.getInputStream());
            file.transferTo(new File(dir, file.getOriginalFilename()));
            if (image == null) {
                log.info("上传失败，内容不符");
                return null;
            }
            String url = HOST + ":" + PORT + "/" + DIR + "/" + file.getOriginalFilename();
            log.info("absolute url: {}", url);
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
