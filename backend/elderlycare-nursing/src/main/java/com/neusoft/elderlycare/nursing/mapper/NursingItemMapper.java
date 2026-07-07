package com.neusoft.elderlycare.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.elderlycare.entity.NursingItem;
import org.apache.ibatis.annotations.Param;

public interface NursingItemMapper extends BaseMapper<NursingItem> {
    // 自定义分页：联查护理级别
    IPage<NursingItem> selectNursingItemPage(Page<NursingItem> page, @Param("keyword") String keyword);
}
