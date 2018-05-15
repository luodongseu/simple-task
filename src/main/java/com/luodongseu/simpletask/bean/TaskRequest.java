package com.luodongseu.simpletask.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

/**
 * 任务请求的请求体
 *
 * @author luodongseu
 */
@Getter
@Setter
public class TaskRequest {

    /**
     * 任务创建者
     */
    private String creator;

    /**
     * 任务基本描述
     */
    private String description;

    /**
     * 执行体信息
     */
    private String execution;

    /**
     * 任务类别，自定义
     */
    private String category;

    /**
     * 当前的备注
     */
    private String note;

    /**
     * 任务开始时间
     */
    private Date startDate;

    /**
     * 任务结束时间
     */
    private Date endDate;

    /**
     * 自定义的属性，可以包含详情等信息
     */
    private Map<String, Object> meta;

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
