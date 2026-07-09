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

    /**
     * 统计客户「未退住」的入住记录数
     * 逻辑：该客户有入住记录，且（无已执行的退住记录 或 最新入住日期 > 最新已执行退住日期）
     * 只看退住日期 <= 今天的记录，未来退住日期不算已退住
     */
    @Select("SELECT COUNT(*) FROM check_in ci WHERE ci.customer_id = #{customerId} AND ci.deleted = 0 " +
            "AND (" +
            "  (SELECT MAX(co.checkout_date) FROM check_out co WHERE co.customer_id = #{customerId} AND co.deleted = 0 AND co.checkout_date <= CURDATE()) IS NULL " +
            "  OR ci.check_in_date > (SELECT MAX(co.checkout_date) FROM check_out co WHERE co.customer_id = #{customerId} AND co.deleted = 0 AND co.checkout_date <= CURDATE())" +
            ")")
    int countActiveCheckIn(@Param("customerId") Long customerId);

    /**
     * 统计床位「未退住」的入住记录数
     * 逻辑：该床位有入住记录，且对应客户未退住
     */
    @Select("SELECT COUNT(*) FROM check_in ci WHERE ci.bed_id = #{bedId} AND ci.deleted = 0 " +
            "AND ci.customer_id IN (SELECT c.id FROM customer c WHERE c.deleted = 0 AND c.checked_in = 1)")
    int countActiveCheckInByBed(@Param("bedId") Long bedId);
}
