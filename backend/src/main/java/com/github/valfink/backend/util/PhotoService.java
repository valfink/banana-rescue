package com.github.valfink.backend.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PhotoService {
    private final Cloudinary cloudinary;

    public PhotoService(@Value("${cloudinary.cloud.name}") String cloudName, @Value("${cloudinary.api.key}") String apiKey, @Value("${cloudinary.api.secret}") String apiSecret) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        cloudinary = new Cloudinary(config);
    }

    public String uploadPhoto(MultipartFile photo) throws IOException {
        Map result = cloudinary.uploader().upload(photo.getBytes(), ObjectUtils.emptyMap());
        return result.get("url").toString();
    }
}
