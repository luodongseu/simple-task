package com.luodongseu.simpletask.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 任务的奖励模板
 * <p>
 * 任务执行单元完成后可能会使用该模板中的信息
 *
 * @author luodongseu
 */
@Getter
@Setter
@Entity
@Table(name = "t_simple_task_reward_template")
public class TaskRewardTemplate {

    @Id
    private String id;

    /**
     * 类型，长度不超过32个字符
     */
    @Column(length = 32)
    private String type;

    /**
     * 奖励描述，可以自定义json串转字符串存储
     */
    private String description;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建任务的时间戳，毫秒单位
     */
    @Column(length = 13)
    private long createTime = System.currentTimeMillis();
}
