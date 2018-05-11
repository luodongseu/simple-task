package com.luodongseu.simpletask.model;

import com.luodongseu.simpletask.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务详情表
 *
 * @author luodong
 */
@Getter
@Setter
@Entity
@Table(name = "t_simple_task")
public class Task {

    @Id
    private String id;

    /**
     * 任务创建者，一般是创建人的ID
     */
    @Column(nullable = false)
    private String creator;

    /**
     * 任务文字描述
     */
    private String description;

    /**
     * 任务详细信息说明，与@description字段可以联合使用或仅用其中一个
     */
    private String detail;

    /**
     * 执行体信息，一般用于帮助第三方服务记录该任务关联的其他模型
     */
    private String execution;

    /**
     * 任务开始的时间戳，毫秒单位
     */
    @Column(length = 11)
    private long startTime;

    /**
     * 任务结束的时间戳，毫秒单位
     */
    @Column(length = 11)
    private long endTime;

    /**
     * 任务类别说明，一般由第三方指定
     */
    private String category;

    /**
     * 任务奖励模板
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "reward_template_id")
    private TaskRewardTemplate rewardTemplate;

    /**
     * 任务状态(未开始，进行中，已结束，已取消，已失效)
     */
    @Column(nullable = false)
    private StatusEnum status;

    /**
     * 创建任务的时间戳，毫秒单位
     */
    @Column(length = 13)
    private long createTime = System.currentTimeMillis();

    /**
     * 备注信息, 以 subject_id 作为外键
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "subjectId")
    private List<Note> notes = new ArrayList<>();
}
