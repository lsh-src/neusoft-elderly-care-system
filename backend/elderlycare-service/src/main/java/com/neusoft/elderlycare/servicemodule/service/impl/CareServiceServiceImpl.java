package com.neusoft.elderlycare.servicemodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.elderlycare.entity.CareService;
import com.neusoft.elderlycare.servicemodule.mapper.CareServiceMapper;
import com.neusoft.elderlycare.servicemodule.service.CareServiceService;
import org.springframework.stereotype.Service;

@Service
public class CareServiceServiceImpl extends ServiceImpl<CareServiceMapper, CareService> implements CareServiceService {
}
