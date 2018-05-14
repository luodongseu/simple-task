package com.luodongseu.simpletask.internal;

import com.luodongseu.simpletask.service.TaskCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 定时执行器，用于监听任务的起始和结束时间，更改任务状态
 *
 * @author luodong
 */
@Service
public class ScheduleRunner {

    private final TaskCheckerService taskCheckerService;

    @Autowired
    public ScheduleRunner(TaskCheckerService endTaskService) {
        this.taskCheckerService = endTaskService;
    }

    /**
     * 定时（启动后延迟60秒，每隔30秒执行一次）执行
     */
    @Scheduled(initialDelay = 60000, fixedDelay = 30000)
    public void run() {
        this.taskCheckerService.checkTaskStartTime();
        this.taskCheckerService.checkTaskEndTime();
    }
}
