package com.seiko.work.module.workstation.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seiko.work.base.Result;
import com.seiko.work.module.workstation.entity.MailMessage;
import com.seiko.work.module.workstation.service.MailMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件管理 Controller
 */
@RestController
@RequestMapping("/api/mails")
@RequiredArgsConstructor
@Validated
@SaCheckLogin
@Tag(name = "邮件管理", description = "邮件列表、详情、标记已读")
public class MailController {

    private final MailMessageService mailMessageService;

    @GetMapping
    @Operation(summary = "邮件列表")
    public Result<Page<MailMessage>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer isRead) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<MailMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailMessage::getUserId, userId);
        if (isRead != null) {
            wrapper.eq(MailMessage::getIsRead, isRead);
        }
        wrapper.orderByDesc(MailMessage::getReceiveTime);
        return Result.success(mailMessageService.page(new Page<>(current, size), wrapper));
    }

    @GetMapping("/{id}")
    @Operation(summary = "邮件详情")
    public Result<MailMessage> getById(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<MailMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailMessage::getId, id)
                .eq(MailMessage::getUserId, userId);
        return Result.success(mailMessageService.getOne(wrapper));
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "标记邮件已读")
    public Result<Void> markRead(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        MailMessage mail = mailMessageService.getById(id);
        if (mail != null && mail.getUserId().equals(userId)) {
            mail.setIsRead(1);
            mailMessageService.updateById(mail);
        }
        return Result.success();
    }

}
