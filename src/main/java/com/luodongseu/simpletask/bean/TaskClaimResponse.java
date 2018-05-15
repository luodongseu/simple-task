package com.luodongseu.simpletask.bean;

import com.luodongseu.simpletask.enums.ClaimCategoryEnum;
import com.luodongseu.simpletask.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 任务执行日志请求的请求体
 *
 * @author luodongseu
 */
@Getter
@Setter
public class TaskClaimResponse {
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
     * 类别
     */
    private ClaimCategoryEnum category;

    /**
     * 元数据，可以自定义json
     */
    private Map<String, Object> meta;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 任务认领状态
     */
    private StatusEnum status;

    /**
     * 备注信息
     */
    private List<NoteResponse> notes = new ArrayList<>();
}
