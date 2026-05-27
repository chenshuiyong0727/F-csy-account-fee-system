package com.yj.accountfee.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yj.accountfee.common.BizException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {
    private final SysUserMapper userMapper;
    private final Map<String, AuthSession> sessions = new ConcurrentHashMap<>();

    @Value("${account-fee.auth.token-expire-hours:24}")
    private long tokenExpireHours;

    public AuthService(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public LoginResponse login(LoginRequest request) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getUsername, request.getUsername()));
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException("账号不存在或已停用");
        }
        if (!sha256(request.getPassword()).equalsIgnoreCase(user.getPassword())) {
            throw new BizException("账号或密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        sessions.put(token, new AuthSession(
            user.getUsername(),
            user.getRealName(),
            user.getRoleCode(),
            LocalDateTime.now().plusHours(tokenExpireHours)
        ));
        return new LoginResponse(token, user.getRealName(), user.getRoleCode());
    }

    public AuthSession requireSession(String token) {
        if (!StringUtils.hasText(token)) {
            throw new BizException("请先登录");
        }
        AuthSession session = sessions.get(token);
        if (session == null || session.getExpireTime().isBefore(LocalDateTime.now())) {
            sessions.remove(token);
            throw new BizException("登录已过期，请重新登录");
        }
        return session;
    }

    public void logout(String token) {
        if (StringUtils.hasText(token)) {
            sessions.remove(token);
        }
    }

    private String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new BizException("密码加密组件不可用");
        }
    }
}
