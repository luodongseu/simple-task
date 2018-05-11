package com.luodongseu.simpletask.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * TaskWhiteList的元模型
 *
 * @author luodongseu
 */
@StaticMetamodel(TaskWhiteList.class)
public class TaskWhiteList_ {
    public static volatile SingularAttribute<TaskWhiteList, String> taskId;
    public static volatile SingularAttribute<TaskWhiteList, String> claimer;
}
