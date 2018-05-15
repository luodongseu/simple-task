package com.luodongseu.simpletask.bean;

import com.luodongseu.simpletask.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 任务实体响应类
 *
 * @author luodongseu
 */
@Getter
@Setter
public class TaskResponse {
    private String id;
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
     * 任务开始时间
     */
    private Date startDate;

    /**
     * 任务状态
     */
    private StatusEnum status;

    /**
     * 任务结束时间
     */
    private Date endDate;

    /**
     * 自定义的属性，可以包含详情等信息
     */
    private Map<String, Object> meta;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 奖励信息
     */
    private TaskRewardTemplateResponse rewardTemplate;

    /**
     * 备注信息
     */
    private List<NoteResponse> notes = new ArrayList<>();
}
