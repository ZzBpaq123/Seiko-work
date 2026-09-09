package com.seiko.work.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seiko.work.base.Result;
import com.seiko.work.base.ResultCode;
import com.seiko.work.exception.BusinessException;
import com.seiko.work.dto.TaskDTO;
import com.seiko.work.entity.Task;
import com.seiko.work.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 工作事项 Controller
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Validated
@SaCheckLogin
@Tag(name = "工作事项", description = "任务增删改查、今日工作")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "任务列表")
    public Result<Page<Task>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getUserId, userId)
                .orderByDesc(Task::getPriority)
                .orderByAsc(Task::getPlanDate);
        return Result.success(taskService.page(new Page<>(current, size), wrapper));
    }

    @GetMapping("/today")
    @Operation(summary = "今日工作")
    public Result<List<Task>> today() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(taskService.listToday(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "任务详情")
    public Result<Task> getById(@PathVariable Long id) {
        Task task = getTaskById(id);
        return Result.success(task);
    }

    @PostMapping
    @Operation(summary = "创建任务")
    public Result<Void> save(@Valid @RequestBody TaskDTO dto) {
        Task task = new Task();
        BeanUtils.copyProperties(dto, task);
        task.setUserId(StpUtil.getLoginIdAsLong());
        taskService.save(task);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新任务")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TaskDTO dto) {
        Task task = getTaskById(id);
        BeanUtils.copyProperties(dto, task);
        taskService.updateById(task);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除任务")
    public Result<Void> delete(@PathVariable Long id) {
        getTaskById(id);
        taskService.removeById(id);
        return Result.success();
    }

    private Task getTaskById(Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        Task task = taskService.getById(id);
        if (task == null || !task.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return task;
    }

}
