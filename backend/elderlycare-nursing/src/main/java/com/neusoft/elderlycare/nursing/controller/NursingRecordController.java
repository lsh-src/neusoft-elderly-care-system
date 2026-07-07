package com.neusoft.elderlycare.nursing.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.ApiResponse;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.controller.BaseCrudController;
import com.neusoft.elderlycare.entity.NursingRecord;
import com.neusoft.elderlycare.nursing.service.NursingRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/nursing-records")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','NURSE','USER')")
@Tag(name = "护理记录", description = "护理执行记录")
public class NursingRecordController extends BaseCrudController<NursingRecord> {
    private final NursingRecordService recordService;

    @Override
    protected IService<NursingRecord> service() {
        return recordService;
    }

    @Override
    protected QueryWrapper<NursingRecord> wrapper(PageQuery query) {
        QueryWrapper<NursingRecord> w = new QueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            w.like("record_no", query.getKeyword());
        }
        return w.orderByDesc("id");
    }

    @Override
    @Operation(summary = "分页查询护理记录", description = "分页查询护理执行记录列表，含客户姓名和项目名称")
    @GetMapping("/page")
    public ApiResponse<IPage<NursingRecord>> page(PageQuery query) {
        return ApiResponse.success(recordService.pageWithName(query));
    }

    @Operation(summary = "确认护理结果", description = "确认护理记录的结果")
    @PostMapping("/{id}/confirm")
    public ApiResponse<Boolean> confirmResult(
            @Parameter(description = "记录ID", required = true) @PathVariable Long id,
            @RequestParam(defaultValue = "正常") String result) {
        return ApiResponse.success(recordService.confirmResult(id, result));
    }

    @Operation(summary = "查询所有护理记录", description = "获取全部护理记录列表（供内部Feign调用）")
    @GetMapping("/list")
    public ApiResponse<List<NursingRecord>> list() {
        return ApiResponse.success(recordService.list());
    }
}
