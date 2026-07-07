package com.neusoft.elderlycare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.elderlycare.common.BusinessException;
import com.neusoft.elderlycare.dto.LoginDTO;
import com.neusoft.elderlycare.dto.PasswordDTO;
import com.neusoft.elderlycare.dto.RegisterDTO;
import com.neusoft.elderlycare.entity.SysUser;
import com.neusoft.elderlycare.mapper.SysUserMapper;
import com.neusoft.elderlycare.security.JwtTokenProvider;
import com.neusoft.elderlycare.service.SysUserService;
import com.neusoft.elderlycare.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    public LoginVO login(LoginDTO dto) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getPhone());
        // loadUserByUsername 已校验用户存在性和启用状态
        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, dto.getPhone()));
        user.setPassword(null);
        return new LoginVO(tokenProvider.generateToken(user.getPhone(), user.getRole()), user);
    }

    @Override
    public SysUser register(RegisterDTO dto) {
        long exists = count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, dto.getPhone()));
        if (exists > 0) {
            throw new BusinessException("手机号已注册");
        }
        SysUser user = new SysUser();
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setAge(dto.getAge());
        user.setGender(dto.getGender());
        user.setRole("ROLE_USER");
        user.setEnabled(1);
        save(user);
        user.setPassword(null);
        return user;
    }

    @Override
    public SysUser currentUser() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone));
        if (user == null) {
            throw new BusinessException("用户不存在或已被删除");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void changePassword(PasswordDTO dto) {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        updateById(user);
    }
}
