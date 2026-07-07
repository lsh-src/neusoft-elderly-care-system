package com.neusoft.elderlycare.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.elderlycare.entity.NursingRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface NursingRecordMapper extends BaseMapper<NursingRecord> {
    IPage<NursingRecord> selectNursingRecordPage(Page<NursingRecord> page, @Param("keyword") String keyword);

    @Select("SELECT record_no FROM nursing_record " +
            "WHERE record_no LIKE CONCAT(#{prefix}, '%') " +
            "ORDER BY record_no DESC LIMIT 1")
    String selectMaxByPrefix(@Param("prefix") String prefix);
}
