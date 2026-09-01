package com.seiko.work.module.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.seiko.work.base.ResultCode;
import com.seiko.work.config.properties.SecurityProperties;
import com.seiko.work.constant.RedisKey;
import com.seiko.work.exception.BusinessException;
import com.seiko.work.module.user.dto.LoginDTO;
import com.seiko.work.module.user.dto.PhoneLoginDTO;
import com.seiko.work.module.user.dto.PhoneRegisterDTO;
import com.seiko.work.module.user.dto.RegisterDTO;
import com.seiko.work.module.user.dto.SendEmailCodeDTO;
import com.seiko.work.module.user.dto.SendPhoneCodeDTO;
import com.seiko.work.module.user.entity.User;
import com.seiko.work.module.user.service.AuthService;
import com.seiko.work.module.user.service.SmsService;
import com.seiko.work.module.user.service.UserService;
import com.seiko.work.module.user.vo.LoginVO;
import com.seiko.work.module.user.vo.UserVO;
import com.seiko.work.util.EmailCodeUtil;
import com.seiko.work.util.IpUtils;
import com.seiko.work.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 认证 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final SmsService smsService;
    private final SecurityProperties securityProperties;
    private final HttpServletRequest request;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    @Override
    public void sendEmailCode(SendEmailCodeDTO dto) {
        if (!Boolean.TRUE.equals(securityProperties.getEmailCode().getEnabled())) {
            throw new BusinessException(ResultCode.ERROR.getCode(), "邮箱验证码功能未启用");
        }

        String email = dto.getEmail();
        String ip = IpUtils.getClientIp(request);

        String ipLockKey = RedisKey.EMAIL_IP_LOCK.format(ip);
        if (redisTemplate.hasKey(ipLockKey)) {
            throw new BusinessException(ResultCode.VERIFICATION_CODE_TOO_FREQUENT);
        }

        String cooldownKey = RedisKey.EMAIL_COOLDOWN.format(email);
        if (redisTemplate.hasKey(cooldownKey)) {
            throw new BusinessException(ResultCode.VERIFICATION_CODE_TOO_FREQUENT);
        }

        // IP 发送次数限制
        String ipCountKey = RedisKey.EMAIL_IP_COUNT.format(ip);
        Long count = redisTemplate.opsForValue().increment(ipCountKey);
        if (count != null && count == 1) {
            redisTemplate.expire(ipCountKey, 1, TimeUnit.HOURS);
        }
        if (count != null && count > securityProperties.getEmailCode().getIpMaxSendsPerHour()) {
            redisTemplate.opsForValue().set(ipLockKey, "1",
                    Duration.ofSeconds(securityProperties.getEmailCode().getIpLockSeconds()));
            throw new BusinessException(ResultCode.VERIFICATION_CODE_TOO_FREQUENT);
        }

        // 生成并存储验证码
        String code = EmailCodeUtil.generate(securityProperties.getEmailCode().getCodeLength());
        String codeKey = RedisKey.EMAIL_CODE.format(email);
        redisTemplate.opsForValue().set(codeKey, code,
                Duration.ofSeconds(securityProperties.getEmailCode().getCodeTtlSeconds()));

        // 设置发送冷却
        redisTemplate.opsForValue().set(cooldownKey, "1",
                Duration.ofSeconds(securityProperties.getEmailCode().getResendCooldownSeconds()));

        // 发送邮件
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(securityProperties.getEmailCode().getSubject());
            message.setText(String.format("您的验证码为：%s，%d 分钟内有效。如非本人操作，请忽略。",
                    code, securityProperties.getEmailCode().getCodeTtlSeconds() / 60));
            message.setFrom(securityProperties.getEmailCode().getSenderName() + " <" + getSenderEmail() + ">");
            mailSender.send(message);
            log.info("已向邮箱 {} 发送验证码", email);
        } catch (Exception e) {
            log.error("发送验证码邮件失败: ", e);
            throw new BusinessException(ResultCode.VERIFICATION_CODE_SEND_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        String email = dto.getEmail();
        String codeKey = RedisKey.EMAIL_CODE.format(email);
        String cachedCode = redisTemplate.opsForValue().get(codeKey);

        if (cachedCode == null || !cachedCode.equals(dto.getCode())) {
            throw new BusinessException(ResultCode.VERIFICATION_CODE_ERROR);
        }

        if (userService.getByUsername(dto.getUsername()) != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        if (userService.getByEmail(email) != null) {
            throw new BusinessException(ResultCode.CONFLICT.getCode(), "该邮箱已被注册");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(email);
        user.setPassword(PasswordUtil.hash(dto.getPassword()));
        user.setStatus(1);
        userService.save(user);

        redisTemplate.delete(codeKey);
        log.info("用户 {} 注册成功", dto.getUsername());
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        String email = dto.getEmail();
        String ip = IpUtils.getClientIp(request);

        if (Boolean.TRUE.equals(securityProperties.getLoginRateLimit().getEnabled())) {
            checkLoginLock(email, ip);
        }

        User user = userService.getByEmail(email);
        if (user == null) {
            handleLoginFailure(email, ip);
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        if (!PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            handleLoginFailure(email, ip);
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 登录成功，清除失败计数
        clearLoginFailure(email, ip);

        StpUtil.login(user.getId());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(StpUtil.getTokenValue());
        loginVO.setTokenName(StpUtil.getTokenName());
        loginVO.setUser(convertToVO(user));

        log.info("用户 {} 登录成功", email);
        return loginVO;
    }

    @Override
    public void sendPhoneCode(SendPhoneCodeDTO dto) {
        if (!Boolean.TRUE.equals(securityProperties.getPhoneCode().getEnabled())) {
            throw new BusinessException(ResultCode.ERROR.getCode(), "手机验证码功能未启用");
        }

        String phone = dto.getPhone();
        String ip = IpUtils.getClientIp(request);

        String ipLockKey = RedisKey.PHONE_IP_LOCK.format(ip);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(ipLockKey))) {
            throw new BusinessException(ResultCode.VERIFICATION_CODE_TOO_FREQUENT);
        }

        String cooldownKey = RedisKey.PHONE_COOLDOWN.format(phone);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new BusinessException(ResultCode.VERIFICATION_CODE_TOO_FREQUENT);
        }

        String ipCountKey = RedisKey.PHONE_IP_COUNT.format(ip);
        Long count = redisTemplate.opsForValue().increment(ipCountKey);
        if (count != null && count == 1) {
            redisTemplate.expire(ipCountKey, 1, TimeUnit.HOURS);
        }
        if (count != null && count > securityProperties.getPhoneCode().getIpMaxSendsPerHour()) {
            redisTemplate.opsForValue().set(ipLockKey, "1",
                    Duration.ofSeconds(securityProperties.getPhoneCode().getIpLockSeconds()));
            throw new BusinessException(ResultCode.VERIFICATION_CODE_TOO_FREQUENT);
        }

        String code = EmailCodeUtil.generate(securityProperties.getPhoneCode().getCodeLength());
        String codeKey = RedisKey.PHONE_CODE.format(phone);
        redisTemplate.opsForValue().set(codeKey, code,
                Duration.ofSeconds(securityProperties.getPhoneCode().getCodeTtlSeconds()));

        redisTemplate.opsForValue().set(cooldownKey, "1",
                Duration.ofSeconds(securityProperties.getPhoneCode().getResendCooldownSeconds()));

        smsService.send(phone, code);
        log.info("已向手机号 {} 发送验证码", phone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void phoneRegister(PhoneRegisterDTO dto) {
        String phone = dto.getPhone();
        String codeKey = RedisKey.PHONE_CODE.format(phone);
        String cachedCode = redisTemplate.opsForValue().get(codeKey);

        if (cachedCode == null || !cachedCode.equals(dto.getCode())) {
            throw new BusinessException(ResultCode.VERIFICATION_CODE_ERROR);
        }

        if (userService.getByUsername(dto.getUsername()) != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        if (userService.getByPhone(phone) != null) {
            throw new BusinessException(ResultCode.CONFLICT.getCode(), "该手机号已被注册");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPhone(phone);
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(PasswordUtil.hash(dto.getPassword()));
        }
        user.setStatus(1);
        userService.save(user);

        redisTemplate.delete(codeKey);
        log.info("用户 {} 手机号注册成功", dto.getUsername());
    }

    @Override
    public LoginVO phoneLogin(PhoneLoginDTO dto) {
        String phone = dto.getPhone();
        String ip = IpUtils.getClientIp(request);

        if (Boolean.TRUE.equals(securityProperties.getLoginRateLimit().getEnabled())) {
            checkLoginLock(phone, ip);
        }

        User user = userService.getByPhone(phone);
        if (user == null) {
            handleLoginFailure(phone, ip);
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        String codeKey = RedisKey.PHONE_CODE.format(phone);
        String cachedCode = redisTemplate.opsForValue().get(codeKey);
        if (cachedCode == null || !cachedCode.equals(dto.getCode())) {
            handleLoginFailure(phone, ip);
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        clearLoginFailure(phone, ip);
        redisTemplate.delete(codeKey);

        StpUtil.login(user.getId());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(StpUtil.getTokenValue());
        loginVO.setTokenName(StpUtil.getTokenName());
        loginVO.setUser(convertToVO(user));

        log.info("用户 {} 手机号登录成功", phone);
        return loginVO;
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
        return convertToVO(user);
    }

    private void checkLoginLock(String email, String ip) {
        String accountLockKey = RedisKey.LOGIN_ACCOUNT_LOCK.format(email);
        String ipLockKey = RedisKey.LOGIN_IP_LOCK.format(ip);
        if (redisTemplate.hasKey(accountLockKey)) {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS.getCode(), "账号已被锁定，请稍后再试");
        }
        if (redisTemplate.hasKey(ipLockKey)) {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS.getCode(), "IP 已被锁定，请稍后再试");
        }
    }

    private void handleLoginFailure(String email, String ip) {
        if (!Boolean.TRUE.equals(securityProperties.getLoginRateLimit().getEnabled())) {
            return;
        }

        long windowSeconds = securityProperties.getLoginRateLimit().getWindowSeconds();
        long lockSeconds = securityProperties.getLoginRateLimit().getLockSeconds();

        incrementFailCount(RedisKey.LOGIN_ACCOUNT_FAIL.format(email),
                securityProperties.getLoginRateLimit().getMaxAttempts(),
                RedisKey.LOGIN_ACCOUNT_LOCK.format(email), windowSeconds, lockSeconds);

        incrementFailCount(RedisKey.LOGIN_IP_FAIL.format(ip),
                securityProperties.getLoginRateLimit().getIpMaxAttempts(),
                RedisKey.LOGIN_IP_LOCK.format(ip), windowSeconds, lockSeconds);
    }

    private void incrementFailCount(String failKey, int maxAttempts, String lockKey, long windowSeconds, long lockSeconds) {
        Long count = redisTemplate.opsForValue().increment(failKey);
        if (count != null && count == 1) {
            redisTemplate.expire(failKey, Duration.ofSeconds(windowSeconds));
        }
        if (count != null && count >= maxAttempts) {
            redisTemplate.opsForValue().set(lockKey, "1", Duration.ofSeconds(lockSeconds));
        }
    }

    private void clearLoginFailure(String email, String ip) {
        if (!Boolean.TRUE.equals(securityProperties.getLoginRateLimit().getEnabled())) {
            return;
        }
        redisTemplate.delete(RedisKey.LOGIN_ACCOUNT_FAIL.format(email));
        redisTemplate.delete(RedisKey.LOGIN_IP_FAIL.format(ip));
    }

    private String getSenderEmail() {
        return senderEmail != null ? senderEmail : "";
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        return vo;
    }

}
