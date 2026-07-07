package com.neusoft.elderlycare.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.elderlycare.entity.Customer;
import com.neusoft.elderlycare.customer.mapper.CustomerMapper;
import com.neusoft.elderlycare.customer.service.CustomerService;
import com.neusoft.elderlycare.util.NumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public boolean save(Customer entity) {
        String no = NumberGenerator.next("C", prefix -> baseMapper.selectMaxByPrefix(prefix));
        entity.setCustomerNo(no);
        log.debug("生成客户编号: {}", no);
        return super.save(entity);
    }

}
