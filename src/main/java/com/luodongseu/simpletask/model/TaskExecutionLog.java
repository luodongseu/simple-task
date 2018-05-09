package com.luodongseu.simpletask.model;

import com.luodongseu.simpletask.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 任务执行日志
 *
 * @author luodongseu
 */
@Getter
@Setter
@Entity
@Table(name = "t_simple_task_execution_log")
public class TaskExecutionLog extends NoteBase {

    @Id
    private String id;

    /**
     * 任务认领信息，以 task_claim_id 作为外键
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_claim_id")
    private TaskClaim taskClaim;

    /**
     * 执行开始的时间戳，毫秒单位
     */
    @Column(length = 11)
    private long startTime;

    /**
     * 执行结束的时间戳，毫秒单位
     */
    @Column(length = 11)
    private long endTime;

    /**
     * 执行的记录日志，第三方可以在该字段放任何数据，以字符串存储
     */
    private Object log;

    /**
     * 任务执行状态： 进行中，已完成
     */
    private StatusEnum status;
}
