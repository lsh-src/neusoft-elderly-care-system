package com.neusoft.elderlycare.nursing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.elderlycare.entity.NurseArea;
import com.neusoft.elderlycare.nursing.mapper.NurseAreaMapper;
import com.neusoft.elderlycare.nursing.service.NurseAreaService;
import org.springframework.stereotype.Service;

// 标准MP写法：继承ServiceImpl + 实现Service接口
@Service
public class NurseAreaServiceImpl
        extends ServiceImpl<NurseAreaMapper, NurseArea>
        implements NurseAreaService {

}