package com.luodongseu.simpletask.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Note模型的响应类
 *
 * @author luodongseu
 */
@Getter
@Setter
public class NoteResponse {
    private String id;

    /**
     * 属主ID
     */
    private String subjectId;

    /**
     * 备注信息
     */
    private String note;

    /**
     * 创建时间
     */
    private Date createTime;
}
