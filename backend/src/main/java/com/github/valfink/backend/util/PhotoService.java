package com.github.valfink.backend.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class PhotoService {
    private final Cloudinary cloudinary;

    public PhotoService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadPhoto(MultipartFile photo) throws IOException {
        Map result = cloudinary.uploader().upload(photo.getBytes(), ObjectUtils.emptyMap());
        return result.get("url").toString();
    }

    public String deletePhoto(String url) throws IOException {
        int startOfPublicId = url.lastIndexOf('/') + 1;
        int endOfPublicId = url.indexOf('.', startOfPublicId);
        String publicId = url.substring(startOfPublicId, endOfPublicId);
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        return result.get("result").toString();
    }
}
