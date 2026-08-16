package com.product_service.controller;

import com.product_service.dto.ApiResponse;
import com.product_service.service.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/product")
public class ImageController {

    private final S3Service s3Service;

    public ImageController(S3Service s3Service) {
        this.s3Service = s3Service;
    }


    @PostMapping("/upload-image")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("brandId") Integer brandId) {

        String imageUrl = s3Service.uploadFile(file, brandId);

        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Image uploaded successfully");
        response.setStatus(200);
        response.setData(imageUrl);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
