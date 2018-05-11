package com.luodongseu.simpletask.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

/**
 * 任务奖励模板的请求体
 *
 * @author luodong
 */
@Getter
@Setter
public class TaskRewardTemplateResponse {
    private String id;

    /**
     * 创建者信息
     */
    private String creator;

    /**
     * 奖励类型，可为空
     */
    private String type;

    /**
     * 奖励元数据，以json串传递接收
     */
    private Map<String, Object> meta;

    /**
     * 创建时间
     */
    private Date createTime;
}
