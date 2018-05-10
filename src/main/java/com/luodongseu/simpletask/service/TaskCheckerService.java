package com.luodongseu.simpletask.service;

/**
 * 监听任务结束的服务
 *
 * @author luodongseu
 */
public interface TaskCheckerService {

    /**
     * 校验任务结束时间
     */
    void checkTaskEndTime();

    /**
     * 校验任务起始时间
     */
    void checkTaskStartTime();
}
