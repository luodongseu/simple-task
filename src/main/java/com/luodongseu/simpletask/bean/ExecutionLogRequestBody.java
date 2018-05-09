package com.luodongseu.simpletask.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 任务执行日志请求的请求体
 *
 * @author luodongseu
 */
@Getter
@Setter
public class ExecutionLogRequestBody {
    private String taskClaimId;
    private String log;
    private String note;
    private Date startDate;
    private Date endDate;
    /**
     * 是否完成
     */
    private boolean complete;
}
