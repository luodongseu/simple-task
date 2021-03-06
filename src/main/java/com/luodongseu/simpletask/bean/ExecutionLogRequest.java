package com.luodongseu.simpletask.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

/**
 * 任务执行日志请求的请求体
 *
 * @author luodongseu
 */
@Getter
@Setter
public class ExecutionLogRequest {

    /**
     * 任务认领模型ID
     */
    private String taskClaimId;

    /**
     * 当前的备注
     */
    private String note;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 自定义的属性，用于记录
     */
    private Map<String, Object> meta;

    /**
     * 是否完成
     */
    private boolean complete;
}
