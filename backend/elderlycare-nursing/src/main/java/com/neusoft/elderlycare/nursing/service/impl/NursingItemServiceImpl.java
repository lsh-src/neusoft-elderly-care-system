package com.neusoft.elderlycare.nursing.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.elderlycare.common.BusinessException;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.NursingItem;
import com.neusoft.elderlycare.entity.NursingLevel;
import com.neusoft.elderlycare.nursing.mapper.NursingItemMapper;
import com.neusoft.elderlycare.nursing.service.NursingItemService;
import com.neusoft.elderlycare.nursing.service.NursingLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NursingItemServiceImpl extends ServiceImpl<NursingItemMapper, NursingItem> implements NursingItemService {
    private final NursingItemMapper itemMapper;
    private final NursingLevelService nursingLevelService;

    @Override
    public IPage<NursingItem> pageWithName(PageQuery query) {
        Page<NursingItem> page = new Page<>(query.getCurrent(), query.getSize());
        return itemMapper.selectNursingItemPage(page, query.getKeyword());
    }

    @Override
    public boolean save(NursingItem entity) {
        // 校验护理级别是否存在
        if (entity.getLevelId() != null) {
            NursingLevel level = nursingLevelService.getById(entity.getLevelId());
            if (level == null) {
                throw new BusinessException("护理级别不存在");
            }
        }
        return super.save(entity);
    }
}
