package com.neusoft.elderlycare.customer.controller;

import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.util.OssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Tag(name = "文件上传", description = "图片上传到阿里云 OSS")
public class UploadController {

    private final OssUtil ossUtil;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    @Operation(summary = "上传文件", description = "上传图片文件到阿里云OSS，支持jpg/png/gif/webp，最大5MB")
    @PostMapping("/file")
    public ApiResponse<?> uploadFile(
            @Parameter(description = "图片文件", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.fail(400, "文件不能为空");
            }
            if (file.getSize() > MAX_SIZE) {
                return ApiResponse.fail(400, "文件大小不能超过 5MB");
            }
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                return ApiResponse.fail(400, "仅支持 jpg/png/gif/webp 格式图片");
            }
            String url = ossUtil.upload(file);
            return ApiResponse.success(url);
        } catch (Exception e) {
            return ApiResponse.fail(500, "上传失败，请稍后重试");
        }
    }
}
