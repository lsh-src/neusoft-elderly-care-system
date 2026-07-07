package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer")
@Schema(description = "入住客户")
public class Customer extends BaseEntity {
    @Schema(description = "客户编号", example = "C20260001")
    private String customerNo;
    @Schema(description = "姓名", example = "李四")
    private String name;
    @Schema(description = "性别", example = "男")
    private String gender;
    @Schema(description = "年龄", example = "75")
    private Integer age;
    @Schema(description = "身份证号", example = "110101195001011234")
    private String idCard;
    @Schema(description = "手机号", example = "13900139000")
    private String phone;
    @Schema(description = "紧急联系人", example = "李明")
    private String emergencyContact;
    @Schema(description = "入住日期", example = "2026-01-15")
    private LocalDate checkInDate;
    @Schema(description = "健康状况")
    private String healthStatus;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "是否在住", example = "1", allowableValues = {"0","1"})
    private Integer checkedIn;
}
