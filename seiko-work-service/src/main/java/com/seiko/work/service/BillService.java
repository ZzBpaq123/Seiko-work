package com.seiko.work.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.dto.BillDTO;
import com.seiko.work.entity.Bill;
import com.seiko.work.enums.BillStatPeriodEnum;
import com.seiko.work.vo.BillCategoryStatVO;
import com.seiko.work.vo.BillOverviewVO;
import com.seiko.work.vo.BillSummaryVO;
import com.seiko.work.vo.BillTrendVO;
import com.seiko.work.vo.BillVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 账单明细 Service
 * <p>
 * 日期区间约定：对外暴露的 startDate / endDate <b>均为闭区间</b>（含当天），
 * 与前端筛选习惯一致；转换到 SQL 的半开区间由实现类内部完成。
 */
public interface BillService extends IService<Bill> {

    /**
     * 分页查询账单明细
     *
     * @param userId    用户ID
     * @param current   当前页
     * @param size      每页条数
     * @param type      账单类型：0-支出，1-收入，null 表示不限
     * @param category  分类，null 或空表示不限
     * @param startDate 起始日（含），null 表示不限
     * @param endDate   结束日（含），null 表示不限
     * @return 分页结果（VO）
     */
    Page<BillVO> pageBills(Long userId, long current, long size, Integer type, String category,
                           LocalDate startDate, LocalDate endDate);

    /**
     * 查询属主校验后的账单，不存在或无权限时抛业务异常
     *
     * @param id     账单ID
     * @param userId 用户ID
     * @return 账单（VO）
     */
    BillVO getOwned(Long id, Long userId);

    /**
     * 新增账单
     *
     * @param userId 用户ID
     * @param dto    请求参数
     */
    void createBill(Long userId, BillDTO dto);

    /**
     * 更新账单（仅更新 DTO 中非空字段）
     *
     * @param id     账单ID
     * @param userId 用户ID
     * @param dto    请求参数
     */
    void updateBill(Long id, Long userId, BillDTO dto);

    /**
     * 删除账单（属主校验，逻辑删除）
     *
     * @param id     账单ID
     * @param userId 用户ID
     */
    void deleteBill(Long id, Long userId);

    /**
     * 指定周期汇总（收入 / 支出 / 结余 / 笔数）
     *
     * @param userId    用户ID
     * @param startDate 起始日（含），null 默认本月 1 号
     * @param endDate   结束日（含），null 默认今天
     * @return 周期汇总
     */
    BillSummaryVO summary(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 概览：今日 / 本周 / 本月 / 本年 四个周期的汇总
     *
     * @param userId 用户ID
     * @return 概览结果
     */
    BillOverviewVO overview(Long userId);

    /**
     * 趋势统计：按天 / 周 / 月 / 年分组
     *
     * @param userId    用户ID
     * @param startDate 起始日（含），null 按粒度取默认区间
     * @param endDate   结束日（含），null 按粒度取默认区间
     * @param period    统计粒度，null 默认按天
     * @return 趋势数据（按分组升序）
     */
    List<BillTrendVO> trend(Long userId, LocalDate startDate, LocalDate endDate, BillStatPeriodEnum period);

    /**
     * 分类维度统计（含支出占比）
     *
     * @param userId    用户ID
     * @param startDate 起始日（含），null 默认本月 1 号
     * @param endDate   结束日（含），null 默认今天
     * @return 分类统计（按支出降序）
     */
    List<BillCategoryStatVO> categoryStat(Long userId, LocalDate startDate, LocalDate endDate);

}
