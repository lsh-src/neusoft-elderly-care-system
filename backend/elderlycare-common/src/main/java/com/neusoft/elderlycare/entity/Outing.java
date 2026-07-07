package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("outing")
@Schema(description = "外出登记")
public class Outing extends BaseEntity {
    @Schema(description = "外出编号", example = "OG20260001")
    private String outingNo;
    @Schema(description = "客户ID", example = "1")
    private Long customerId;
    @Schema(description = "外出时间")
    private LocalDateTime outTime;
    @Schema(description = "预计返回时间")
    private LocalDateTime expectedReturnTime;
    @Schema(description = "实际返回时间")
    private LocalDateTime actualReturnTime;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "状态", example = "外出中", allowableValues = {"外出中","已返回"})
    private String status;
    @Schema(description = "客户姓名（联查字段）")
    @TableField(exist = false)
    private String customerName;
}
