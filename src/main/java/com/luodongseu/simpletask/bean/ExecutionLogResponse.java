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
public class ExecutionLogResponse {
    private String id;

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
     * 任务认领信息
     */
    private TaskClaimResponse taskClaim;

    /**
     * 备注信息
     */
    private List<Note> notes = new ArrayList<>();
}
