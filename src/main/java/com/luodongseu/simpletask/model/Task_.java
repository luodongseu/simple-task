package com.luodongseu.simpletask.model;

import com.luodongseu.simpletask.enums.StatusEnum;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Task的元模型
 *
 * @author luodong
 */
@StaticMetamodel(Task.class)
public class Task_ {
    public static volatile SingularAttribute<Task, String> creator;
    public static volatile SingularAttribute<Task, Long> startTime;
    public static volatile SingularAttribute<Task, Long> endTime;
    public static volatile SingularAttribute<Task, StatusEnum> status;
}
