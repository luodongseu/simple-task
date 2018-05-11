package com.luodongseu.simpletask.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luodongseu.simpletask.bean.NoteResponse;
import com.luodongseu.simpletask.bean.TaskResponse;
import com.luodongseu.simpletask.bean.TaskRewardTemplateResponse;
import com.luodongseu.simpletask.model.Note;
import com.luodongseu.simpletask.model.Task;
import com.luodongseu.simpletask.model.TaskRewardTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模型转换工具类
 *
 * @author luodongseu
 */
@Slf4j
public class ModelUtils {
    /**
     * json解析器
     */
    private static ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static TypeReference<Map<String, Object>> MAP_REFERENCE = new TypeReference<Map<String, Object>>() {
        @Override
        public Type getType() {
            return super.getType();
        }
    };

    /**
     * 将对象转换成json字符串
     *
     * @param o 对象
     * @return 字符串
     */
    public static String writeValueAsString(Object o) {
        if (null == o) {
            return Strings.EMPTY;
        }
        try {
            return JSON_MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.warn("Json parse object {} meta failed", o, e);
            return Strings.EMPTY;
        }
    }

    /**
     * 任务模型转响应体
     *
     * @param task 任务表模型
     * @return TaskResponse
     */
    public static TaskResponse convertToResponse(Task task) {
        if (task == null) {
            return null;
        }
        TaskResponse taskResponse = new TaskResponse();
        BeanUtils.copyProperties(task, taskResponse);
        if (!StringUtils.isEmpty(task.getMeta())) {
            try {
                taskResponse.setMeta(JSON_MAPPER.readValue(task.getMeta(), MAP_REFERENCE));
            } catch (IOException e) {
                taskResponse.setMeta(new HashMap<>(0));
            }
        }
        taskResponse.setRewardTemplate(convertToResponse(task.getRewardTemplate()));
        if (task.getStartTime() > 0) {
            taskResponse.setStartDate(new Date(task.getStartTime()));
        }
        if (task.getEndTime() > 0) {
            taskResponse.setEndDate(new Date(task.getEndTime()));
        }
        taskResponse.setNotes(convertToNoteResponse(task.getNotes()));
        return taskResponse;
    }

    /**
     * 任务模型集合转响应体集合
     *
     * @param tasks 任务表模型集合
     * @return TaskResponse
     */
    public static List<TaskResponse> convertToTaskResponse(List<Task> tasks) {
        if (tasks == null || tasks.size() == 0) {
            return new ArrayList<>();
        }
        return tasks.stream().map(ModelUtils::convertToResponse).collect(Collectors.toList());
    }

    /**
     * 奖励模型转响应体
     *
     * @param rewardTemplate 奖励模板表模型
     * @return TaskRewardTemplateResponse
     */
    public static TaskRewardTemplateResponse convertToResponse(TaskRewardTemplate rewardTemplate) {
        if (rewardTemplate == null) {
            return null;
        }
        TaskRewardTemplateResponse rewardTemplateResponse = new TaskRewardTemplateResponse();
        BeanUtils.copyProperties(rewardTemplate, rewardTemplateResponse);
        try {
            rewardTemplateResponse.setMeta(JSON_MAPPER.readValue(rewardTemplate.getDescription(), MAP_REFERENCE));
        } catch (IOException e) {
            rewardTemplateResponse.setMeta(new HashMap<>(0));
        }
        return rewardTemplateResponse;
    }

    /**
     * 任务模型集合转响应体集合
     *
     * @param rewardTemplates 任务表模型集合
     * @return TaskResponse
     */
    public static List<TaskRewardTemplateResponse> convertToRewardResponse(List<TaskRewardTemplate> rewardTemplates) {
        if (rewardTemplates == null || rewardTemplates.size() == 0) {
            return new ArrayList<>();
        }
        return rewardTemplates.stream().map(ModelUtils::convertToResponse).collect(Collectors.toList());
    }

    /**
     * 备注模型转响应体
     *
     * @param note 备注模型表模型
     * @return NoteResponse
     */
    public static NoteResponse convertToResponse(Note note) {
        if (note == null) {
            return null;
        }
        NoteResponse noteResponse = new NoteResponse();
        BeanUtils.copyProperties(note, noteResponse);
        if (note.getCreateTime() > 0) {
            noteResponse.setCreateTime(new Date(note.getCreateTime()));
        }
        return noteResponse;
    }


    /**
     * 备注模型集合转响应体集合
     *
     * @param notes 备注表模型集合
     * @return TaskResponse
     */
    public static List<NoteResponse> convertToNoteResponse(List<Note> notes) {
        if (notes == null || notes.size() == 0) {
            return new ArrayList<>();
        }
        return notes.stream().map(ModelUtils::convertToResponse).collect(Collectors.toList());
    }

}
