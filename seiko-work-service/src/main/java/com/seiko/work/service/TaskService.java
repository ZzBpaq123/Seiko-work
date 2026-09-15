package com.seiko.work.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.dto.TaskDTO;
import com.seiko.work.entity.Task;
import com.seiko.work.vo.TaskVO;

import java.util.List;

/**
 * 工作事项 Service
 */
public interface TaskService extends IService<Task> {

    /**
     * 分页查询工作事项
     *
     * @param userId  用户ID
     * @param current 当前页
     * @param size    每页条数
     * @return 分页结果（VO）
     */
    Page<TaskVO> pageTasks(Long userId, long current, long size);

    /**
     * 查询今日工作事项（当天及逾期未完成的）
     *
     * @param userId 用户ID
     * @return 工作事项列表（VO）
     */
    List<TaskVO> listToday(Long userId);

    /**
     * 查询属主校验后的工作事项，不存在或无权限时抛业务异常
     *
     * @param id     工作事项ID
     * @param userId 用户ID
     * @return 工作事项（VO）
     */
    TaskVO getOwned(Long id, Long userId);

    /**
     * 创建工作事项
     *
     * @param userId 用户ID
     * @param dto    请求参数
     */
    void createTask(Long userId, TaskDTO dto);

    /**
     * 更新工作事项（仅更新 DTO 中非空字段）
     *
     * @param id     工作事项ID
     * @param userId 用户ID
     * @param dto    请求参数
     */
    void updateTask(Long id, Long userId, TaskDTO dto);

    /**
     * 删除工作事项（属主校验）
     *
     * @param id     工作事项ID
     * @param userId 用户ID
     */
    void deleteTask(Long id, Long userId);

}
