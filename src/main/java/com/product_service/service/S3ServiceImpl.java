package com.product_service.service;

import com.product_service.entity.Brand;
import com.product_service.entity.Image;
import com.product_service.exception.InvalidFileException;
import com.product_service.exception.ResourceNotFoundException;
import com.product_service.repository.BrandRepository;
import com.product_service.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

    private ImageRepository imageRepository;
    private BrandRepository brandRepository;

    private final S3Client s3Client;

    @Value("${aws.bucket-name}")
    private String bucketName;

    public S3ServiceImpl(ImageRepository imageRepository, BrandRepository brandRepository, S3Client s3Client) {
        this.imageRepository = imageRepository;
        this.brandRepository = brandRepository;
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile file, Integer brandId) {
        try {

            String contentType = file.getContentType();
            if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
                throw new InvalidFileException("Only JPG and PNG files are allowed. Received: " + contentType);
            }

            //  File size check (max 5MB)
            long maxSizeInBytes = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSizeInBytes) {
                throw new InvalidFileException("File size exceeds 5MB limit. Uploaded file size: "
                        + (file.getSize() / (1024 * 1024)) + "MB");
            }


            //  Validation 3: Count limit check (max 12 images per brand)
            long currentImageCount = imageRepository.countByBrandId(brandId);
            if (currentImageCount >= 12) {
                throw new InvalidFileException("Maximum 12 images allowed per brand. Current count: " + currentImageCount);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(
                            file.getInputStream(), file.getSize()));
            String url =  "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

            Brand brand = brandRepository.findById(brandId)
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + brandId));
            Image image = new Image();
            image.setBrand(brand);
            image.setUrl(url);

            imageRepository.save(image);

            return  url;

        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}
