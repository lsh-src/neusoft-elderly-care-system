package com.neusoft.elderlycare.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.elderlycare.common.BusinessException;
import com.neusoft.elderlycare.entity.NursingLevel;
import com.neusoft.elderlycare.nursing.mapper.NursingLevelMapper;
import com.neusoft.elderlycare.nursing.service.NursingLevelService;
import org.springframework.stereotype.Service;

@Service
public class NursingLevelServiceImpl extends ServiceImpl<NursingLevelMapper, NursingLevel> implements NursingLevelService {
    @Override
    public boolean save(NursingLevel entity) {
        baseMapper.physicallyRemoveConflict(entity.getLevelName());
        return super.save(entity);
    }

    @Override
    public boolean updateById(NursingLevel entity) {
        long count = count(new LambdaQueryWrapper<NursingLevel>()
                .eq(NursingLevel::getLevelName, entity.getLevelName())
                .ne(NursingLevel::getId, entity.getId()));
        if (count > 0) {
            throw new BusinessException("护理级别名称【" + entity.getLevelName() + "】已存在");
        }
        return super.updateById(entity);
    }
}
