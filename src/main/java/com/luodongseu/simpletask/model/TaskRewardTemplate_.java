package com.luodongseu.simpletask.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * TaskRewardTemplate的元模型
 *
 * @author luodongseu
 */
@StaticMetamodel(TaskRewardTemplate.class)
public class TaskRewardTemplate_ {
    public static volatile SingularAttribute<TaskRewardTemplate, String> id;
    public static volatile SingularAttribute<TaskRewardTemplate, String> creator;
}
