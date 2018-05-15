package com.luodongseu.simpletask.bean;

import com.luodongseu.simpletask.enums.ClaimCategoryEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 任务认领的请求体
 *
 * @author luodongseu
 */
@Getter
@Setter
public class TaskClaimRequest {

    /**
     * 认领者
     */
    private String claimer;

    /**
     * 类别
     */
    private ClaimCategoryEnum category;


}
