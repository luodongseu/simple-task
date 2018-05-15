package com.luodongseu.simpletask.model;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * TaskExecutionLog的元模型
 *
 * @author luodong
 */
@StaticMetamodel(TaskExecutionLog.class)
public class TaskExecutionLog_ {
    public static volatile SingularAttribute<TaskExecutionLog, String> id;
    public static volatile SingularAttribute<TaskExecutionLog, TaskClaim> taskClaim;
}
