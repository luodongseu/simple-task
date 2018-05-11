package com.luodongseu.simpletask.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 任务的白名单
 * <p>
 * 一般用于管理任务的可见性
 *
 * @author luodongseu
 */
@Getter
@Setter
@Entity
@Table(name = "t_simple_task_whitelist")
public class TaskWhiteList {

    @Id
    private String id;

    /**
     * 任务
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "task_id")
    private Task task;

    /**
     * 认领者
     */
    private String claimer;

    /**
     * 创建任务的时间戳，毫秒单位
     */
    @Column(length = 13)
    private long createTime = System.currentTimeMillis();
}
