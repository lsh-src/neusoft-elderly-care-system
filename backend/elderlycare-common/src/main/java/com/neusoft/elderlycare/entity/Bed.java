package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bed")
@Schema(description = "床位")
public class Bed extends BaseEntity {
    @Schema(description = "房间号", example = "A101")
    private String roomNo;
    @Schema(description = "床位号", example = "01")
    private String bedNo;
    @Schema(description = "状态", example = "空闲", allowableValues = {"空闲","已入住","维修中"})
    private String status;
    @Schema(description = "客户ID")
    private Long customerId;
    // ===================== 新增：客户姓名（非数据库字段） =====================
    @Schema(description = "客户姓名（联查字段）")
    @TableField(exist = false)
    private String customerName;
}
