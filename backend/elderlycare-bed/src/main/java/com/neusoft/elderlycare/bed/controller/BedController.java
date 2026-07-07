    package com.neusoft.elderlycare.bed.controller;

    import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
    import com.baomidou.mybatisplus.core.metadata.IPage;
    import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
    import com.baomidou.mybatisplus.extension.service.IService;
    import com.neusoft.elderlycare.common.ApiResponse;
    import com.neusoft.elderlycare.common.PageQuery;
    import com.neusoft.elderlycare.controller.BaseCrudController;
    import com.neusoft.elderlycare.entity.Bed;
    import com.neusoft.elderlycare.bed.service.BedService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.util.StringUtils;
    import org.springframework.web.bind.annotation.*;

    import java.util.Map;

    @RestController
    @RequestMapping("/beds")
    @RequiredArgsConstructor
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Tag(name = "床位管理", description = "床位 CRUD、分配、释放")
    public class BedController extends BaseCrudController<Bed> {
        private final BedService bedService;

        @Override
        protected IService<Bed> service() {
            return bedService;
        }

        @Override
        protected QueryWrapper<Bed> wrapper(PageQuery query) {
            QueryWrapper<Bed> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(query.getKeyword())) {
                wrapper.like("room_no", query.getKeyword()).or().like("bed_no", query.getKeyword()).or().like("status", query.getKeyword());
            }
            return wrapper.orderByDesc("id");
        }
        @Override
        @Operation(summary = "分页查询床位", description = "分页查询床位列表，含客户信息")
        @GetMapping("/page")
        public ApiResponse<IPage<Bed>> page(PageQuery query) {
            return ApiResponse.success(bedService.pageWithCustomer(query));
        }
        @Operation(summary = "分配床位", description = "将床位分配给指定客户")
        @PostMapping("/{id}/assign/{customerId}")
        public ApiResponse<Void> assign(@Parameter(description = "床位ID", required = true, example = "1") @PathVariable Long id, @Parameter(description = "客户ID", required = true, example = "1") @PathVariable Long customerId) {
            bedService.assign(id, customerId);
            return ApiResponse.success();
        }

        @Operation(summary = "释放床位", description = "释放指定床位")
        @PostMapping("/{id}/release")
        public ApiResponse<Void> release(@Parameter(description = "床位ID", required = true, example = "1") @PathVariable Long id) {
            bedService.release(id);
            return ApiResponse.success();
        }

        @Operation(summary = "床位状态统计", description = "统计各状态床位数量")
        @GetMapping("/stats")
        public ApiResponse<Map<String, Long>> stats() {
            return ApiResponse.success(bedService.statusStats());
        }
    }
