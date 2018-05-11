package com.luodongseu.simpletask.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TaskRequest {
    private String creator;
    private String description;
    private String detail;
    private String execution;
    private String category;
    private Date startDate;
    private Date endDate;

    /**
     * 奖励模板ID，可为空
     */
    private String rewardTemplateId;

    /**
     * 白名单
     */
    @JsonProperty
    private String[] whiteList;
}
