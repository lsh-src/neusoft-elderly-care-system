package com.neusoft.elderlycare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("nursing_record")
@Schema(description = "护理记录")
public class NursingRecord extends BaseEntity {
    @Schema(description = "记录编号", example = "NR20260001")
    private String recordNo;
    @Schema(description = "客户ID", example = "1")
    private Long customerId;
    @Schema(description = "护理项目ID", example = "1")
    private Long itemId;
    @Schema(description = "护理人员姓名", example = "护士小王")
    private String nurseName;
    @Schema(description = "护理时间")
    private LocalDateTime nursingTime;
    @Schema(description = "护理结果")
    private String result;
    @Schema(description = "备注")
    private String remark;
    // 联查展示字段
    @Schema(description = "客户姓名（联查字段）")
    @TableField(exist = false)
    private String customerName;  // 客户姓名
    @Schema(description = "护理项目名称（联查字段）")
    @TableField(exist = false)
    private String itemName;      // 护理项目
    @Schema(description = "护理内容（联查字段）")
    @TableField(exist = false)
    private String description;   // 护理内容
}
