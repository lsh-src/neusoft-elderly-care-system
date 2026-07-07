package com.neusoft.elderlycare.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.elderlycare.entity.NursingLevel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface NursingLevelMapper extends BaseMapper<NursingLevel> {
    @Update("DELETE FROM nursing_level WHERE level_name = #{levelName} AND deleted = 1")
    int physicallyRemoveConflict(@Param("levelName") String levelName);
}
