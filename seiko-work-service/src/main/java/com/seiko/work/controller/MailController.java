package com.seiko.work.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.seiko.work.base.Result;
import com.seiko.work.dto.MailAccountDTO;
import com.seiko.work.entity.MailAccount;
import com.seiko.work.entity.MailMessage;
import com.seiko.work.service.MailAccountService;
import com.seiko.work.service.MailMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 邮件管理 Controller
 */
@RestController
@RequestMapping("/api/mails")
@RequiredArgsConstructor
@Validated
@SaCheckLogin
@Tag(name = "邮件管理", description = "邮箱授权配置与邮件实时获取")
public class MailController {

    private final MailAccountService mailAccountService;
    private final MailMessageService mailMessageService;

    @PostMapping("/account")
    @Operation(summary = "保存邮箱授权信息")
    public Result<Void> saveAccount(@Valid @RequestBody MailAccountDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        MailAccount account = mailAccountService.getByUserId(userId);
        if (account == null) {
            account = new MailAccount();
            account.setUserId(userId);
        }
        BeanUtils.copyProperties(dto, account);
        mailAccountService.saveOrUpdate(account);
        return Result.success();
    }

    @GetMapping("/account")
    @Operation(summary = "查询邮箱授权配置")
    public Result<MailAccount> getAccount() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(mailAccountService.getByUserId(userId));
    }

    @GetMapping
    @Operation(summary = "获取所有邮件")
    public Result<List<MailMessage>> listAll() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(mailMessageService.listAll(userId));
    }

    @GetMapping("/{messageUid}")
    @Operation(summary = "邮件详情")
    public Result<MailMessage> getDetail(@PathVariable String messageUid) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(mailMessageService.getDetail(userId, messageUid));
    }

    @PostMapping("/{messageUid}/read")
    @Operation(summary = "标记邮件已读")
    public Result<Void> markRead(@PathVariable String messageUid) {
        Long userId = StpUtil.getLoginIdAsLong();
        mailMessageService.markRead(userId, messageUid);
        return Result.success();
    }

}
