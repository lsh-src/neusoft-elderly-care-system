package com.neusoft.elderlycare.nursing.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.elderlycare.common.BusinessException;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.Customer;
import com.neusoft.elderlycare.entity.NursingItem;
import com.neusoft.elderlycare.entity.NursingRecord;
import com.neusoft.elderlycare.feign.CustomerFeignClient;
import com.neusoft.elderlycare.nursing.mapper.NursingRecordMapper;
import com.neusoft.elderlycare.nursing.service.NursingItemService;
import com.neusoft.elderlycare.nursing.service.NursingRecordService;
import com.neusoft.elderlycare.util.NumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NursingRecordServiceImpl extends ServiceImpl<NursingRecordMapper, NursingRecord> implements NursingRecordService {
    private final NursingRecordMapper recordMapper;
    private final CustomerFeignClient customerFeignClient;
    private final NursingItemService nursingItemService;

    @Override
    public boolean save(NursingRecord entity) {
        // 校验客户是否存在
        if (entity.getCustomerId() != null) {
            Customer c = customerFeignClient.getById(entity.getCustomerId()).getData();
            if (c == null) {
                throw new BusinessException("客户不存在");
            }
        }
        // 校验护理项目是否存在
        if (entity.getItemId() != null) {
            NursingItem item = nursingItemService.getById(entity.getItemId());
            if (item == null) {
                throw new BusinessException("护理项目不存在");
            }
        }
        entity.setRecordNo(NumberGenerator.next("R", prefix -> baseMapper.selectMaxByPrefix(prefix)));
        return super.save(entity);
    }

    @Override
    public IPage<NursingRecord> pageWithName(PageQuery query) {
        Page<NursingRecord> page = new Page<>(query.getCurrent(), query.getSize());
        return recordMapper.selectNursingRecordPage(page, query.getKeyword());
    }

    @Override
    public boolean confirmResult(Long id, String result) {
        NursingRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("护理记录不存在");
        }
        if ("已完成".equals(record.getResult())) {
            throw new BusinessException("该记录已确认完成，不能重复操作");
        }
        record.setResult(result);
        return updateById(record);
    }
}
