package com.luodongseu.simpletask.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 任务请求的请求体
 *
 * @author luodongseu
 */
@Getter
@Setter
public class TaskWhiteListResponse {
    private String id;

    /**
     * 任务信息
     */
    private TaskResponse task;

    /**
     * 认领者
     */
    private String claimer;

    /**
     * 创建时间
     */
    private Date createTime;
}
