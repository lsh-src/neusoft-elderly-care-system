package com.neusoft.elderlycare.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.elderlycare.entity.CheckIn;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CheckInMapper extends BaseMapper<CheckIn> {
    // 自定义分页（联表查询客户姓名+床位名）
    IPage<CheckIn> selectCheckInPage(Page<CheckIn> page, @Param("keyword") String keyword);
    @Select("SELECT register_no FROM check_in " +
            "WHERE register_no LIKE CONCAT(#{prefix}, '%') " +
            "ORDER BY register_no DESC LIMIT 1")
    String selectMaxByPrefix(@Param("prefix") String prefix);
}
