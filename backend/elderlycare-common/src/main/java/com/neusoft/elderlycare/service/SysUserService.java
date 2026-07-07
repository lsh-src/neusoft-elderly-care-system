package com.neusoft.elderlycare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.elderlycare.dto.LoginDTO;
import com.neusoft.elderlycare.dto.PasswordDTO;
import com.neusoft.elderlycare.dto.RegisterDTO;
import com.neusoft.elderlycare.entity.SysUser;
import com.neusoft.elderlycare.vo.LoginVO;

public interface SysUserService extends IService<SysUser> {
    LoginVO login(LoginDTO dto);
    SysUser register(RegisterDTO dto);
    SysUser currentUser();
    void changePassword(PasswordDTO dto);
}
