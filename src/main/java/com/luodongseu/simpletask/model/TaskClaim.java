package com.luodongseu.simpletask.model;

import com.luodongseu.simpletask.enums.ClaimCategoryEnum;
import com.luodongseu.simpletask.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 任务认领记录表
 *
 * @author luodong
 */
@Setter
@Getter
@Entity
@Table(name = "t_simple_task_claim")
public class TaskClaim extends NoteBase {
    /**
     * 任务详情的ID
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "task_id")
    private Task task;

    /**
     * 认领者，一般是认领人的ID
     */
    @Column(nullable = false)
    private String claimer;

    /**
     * 任务认领的类别
     */
    private ClaimCategoryEnum category;

    /**
     * 任务认领完成的进度描述
     */
    private String progress;

    /**
     * 任务认领状态： 进行中，已完成，失效
     */
    @Column(nullable = false)
    private StatusEnum status;

    /**
     * 创建任务的时间戳，毫秒单位
     */
    @Column(length = 13)
    private long createTime = System.currentTimeMillis();
}
