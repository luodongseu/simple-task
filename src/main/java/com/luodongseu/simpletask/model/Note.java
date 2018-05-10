package com.luodongseu.simpletask.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 备注信息表
 *
 * @author luodongseu
 */
@Setter
@Getter
@Entity
@Table(name = "t_simple_task_note")
public class Note {

    /**
     * ID自增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * 属主ID
     */
    @Column(nullable = false)
    private String subjectId;

    /**
     * 记录的备注信息
     */
    private String note;

    @Column(length = 13)
    private long createTime = System.currentTimeMillis();
}
