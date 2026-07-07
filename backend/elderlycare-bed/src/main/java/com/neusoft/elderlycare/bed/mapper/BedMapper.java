package com.neusoft.elderlycare.bed.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.elderlycare.entity.Bed;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface BedMapper extends BaseMapper<Bed> {
    IPage<Bed> selectBedPage(Page<Bed> page, @Param("keyword") String keyword);
    @Update("DELETE FROM bed WHERE room_no = #{roomNo} AND bed_no = #{bedNo} AND deleted = 1")
    int physicallyRemoveConflict(@Param("roomNo") String roomNo, @Param("bedNo") String bedNo);
}
