package com.luodongseu.simpletask.model;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * TaskClaim的元模型
 *
 * @author luodong
 */
@StaticMetamodel(TaskClaim.class)
public class TaskClaim_ {
    public static volatile SingularAttribute<TaskClaim, String> id;
    public static volatile SingularAttribute<TaskClaim, Task> task;
}
