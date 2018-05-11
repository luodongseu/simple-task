package com.luodongseu.simpletask.bean;

import com.luodongseu.simpletask.model.Note;
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
    private String category;

    /**
     * 元数据，可以自定义json
     */
    private Map<String, Object> meta;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 备注信息
     */
    private List<Note> notes = new ArrayList<>();
}
