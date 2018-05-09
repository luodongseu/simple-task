package com.luodongseu.simpletask.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 任务请求的请求体
 *
 * @author luodongseu
 */
@Getter
@Setter
public class TaskRequestBody {
    private String creator;
    private String description;
    private String detail;
    private String category;
    private Date startDate;
    private Date endDate;
}
