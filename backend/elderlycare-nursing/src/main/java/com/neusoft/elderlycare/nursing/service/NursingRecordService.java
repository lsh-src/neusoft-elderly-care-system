package com.neusoft.elderlycare.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.common.PageQuery;
import com.neusoft.elderlycare.entity.NursingRecord;

public interface NursingRecordService extends IService<NursingRecord> {
    IPage<NursingRecord> pageWithName(PageQuery query);
    boolean confirmResult(Long id, String result);
}
