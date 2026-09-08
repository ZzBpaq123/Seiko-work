package com.seiko.work.service;

import com.seiko.work.vo.HolidayVO;

import java.util.List;

/**
 * 节假日 Service
 */
public interface HolidayService {

    /**
     * 查询某年节假日（含调休放假与补班）
     */
    List<HolidayVO> getHolidays(int year);

}
