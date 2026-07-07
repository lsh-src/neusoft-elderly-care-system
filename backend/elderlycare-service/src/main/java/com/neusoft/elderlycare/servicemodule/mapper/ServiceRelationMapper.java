package com.neusoft.elderlycare.servicemodule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.elderlycare.entity.ServiceRelation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ServiceRelationMapper extends BaseMapper<ServiceRelation> {
    // 和 XML id 完全一致：selectServiceRelationPage
    IPage<ServiceRelation> selectServiceRelationPage(Page<ServiceRelation> page, @Param("keyword") String keyword);
    @Update("DELETE FROM service_relation WHERE customer_id = #{customerId} AND manager_id = #{managerId} AND deleted = 1")
    int physicallyRemoveConflict(@Param("customerId") Long customerId, @Param("managerId") Long managerId);
}
