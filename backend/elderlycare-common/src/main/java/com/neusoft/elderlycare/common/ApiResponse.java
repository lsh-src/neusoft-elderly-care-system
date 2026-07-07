package com.neusoft.elderlycare.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一API响应")
public class ApiResponse<T> {
    @Schema(description = "状态码", example = "200")
    private Integer code;
    @Schema(description = "响应消息", example = "success")
    private String message;
    @Schema(description = "响应数据")
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(200, "success", null);
    }

    public static ApiResponse<Void> fail(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
