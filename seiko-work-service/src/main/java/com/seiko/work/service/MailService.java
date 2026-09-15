package com.seiko.work.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.dto.MailDTO;
import com.seiko.work.entity.Mail;
import com.seiko.work.vo.MailAccountVO;

/**
 * 邮箱账号配置 Service
 */
public interface MailService extends IService<Mail> {

    /**
     * 根据用户ID查询邮箱账号配置
     *
     * @param userId 用户ID
     * @return 邮箱账号配置
     */
    Mail getByUserId(Long userId);

    /**
     * 保存邮箱授权配置（服务器配置缺省时按邮箱后缀匹配服务商自动补全）
     *
     * @param userId 用户ID
     * @param dto    请求参数
     */
    void saveAccount(Long userId, MailDTO dto);

    /**
     * 查询邮箱授权配置（VO，不含授权码）
     *
     * @param userId 用户ID
     * @return 邮箱账号配置 VO，未配置返回 null
     */
    MailAccountVO getAccountVO(Long userId);

}
