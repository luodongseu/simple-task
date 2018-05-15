package com.luodongseu.simpletask.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luodongseu.simpletask.bean.*;
import com.luodongseu.simpletask.model.*;
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
     * 任务白名单模型转响应体
     *
     * @param whiteList 任务白名单模型
     * @return TaskWhiteListResponse
     */
    public static TaskWhiteListResponse convertToResponse(TaskWhiteList whiteList) {
        if (whiteList == null) {
            return null;
        }
        TaskWhiteListResponse whiteListResponse = new TaskWhiteListResponse();
        BeanUtils.copyProperties(whiteList, whiteListResponse);
        if (whiteList.getCreateTime() > 0) {
            whiteListResponse.setCreateTime(new Date(whiteList.getCreateTime()));
        }
        whiteListResponse.setTask(convertToResponse(whiteList.getTask()));
        return whiteListResponse;
    }

    /**
     * 任务模型集合转响应体集合
     *
     * @param tasks 任务表模型集合
     * @return List<TaskResponse>
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
     * @return List<TaskRewardTemplateResponse>
     */
    public static List<TaskRewardTemplateResponse> convertToRewardResponse(List<TaskRewardTemplate> rewardTemplates) {
        if (rewardTemplates == null || rewardTemplates.size() == 0) {
            return new ArrayList<>();
        }
        return rewardTemplates.stream().map(ModelUtils::convertToResponse).collect(Collectors.toList());
    }

    /**
     * 任务白名单集合转响应体集合
     *
     * @param whiteLists 任务白名单集合
     * @return List<TaskWhiteListResponse>
     */
    public static List<TaskWhiteListResponse> convertToWhiteListResponse(List<TaskWhiteList> whiteLists) {
        if (whiteLists == null || whiteLists.size() == 0) {
            return new ArrayList<>();
        }
        return whiteLists.stream().map(ModelUtils::convertToResponse).collect(Collectors.toList());
    }


    /**
     * 认领模型转响应体
     *
     * @param claim 认领模型
     * @return TaskClaimResponse
     */
    public static TaskClaimResponse convertToResponse(TaskClaim claim) {
        if (claim == null) {
            return null;
        }
        TaskClaimResponse taskClaimResponse = new TaskClaimResponse();
        BeanUtils.copyProperties(claim, taskClaimResponse);
        if (claim.getCreateTime() > 0) {
            taskClaimResponse.setCreateTime(new Date(claim.getCreateTime()));
        }
        taskClaimResponse.setTask(convertToResponse(claim.getTask()));
        try {
            taskClaimResponse.setMeta(JSON_MAPPER.readValue(claim.getMeta(), MAP_REFERENCE));
        } catch (IOException e) {
            taskClaimResponse.setMeta(new HashMap<>(0));
        }
        taskClaimResponse.setNotes(convertToNoteResponse(claim.getNotes()));
        return taskClaimResponse;
    }

    /**
     * 认领模型集合转响应体集合
     *
     * @param claims 认领表模型集合
     * @return List<TaskClaimResponse>
     */
    public static List<TaskClaimResponse> convertToClaimResponse(List<TaskClaim> claims) {
        if (claims == null || claims.size() == 0) {
            return new ArrayList<>();
        }
        return claims.stream().map(ModelUtils::convertToResponse).collect(Collectors.toList());
    }

    /**
     * 执行日志模型转响应体
     *
     * @param log 执行日志模型
     * @return ExecutionLogResponse
     */
    public static ExecutionLogResponse convertToResponse(TaskExecutionLog log) {
        if (log == null) {
            return null;
        }
        ExecutionLogResponse executionLogResponse = new ExecutionLogResponse();
        BeanUtils.copyProperties(log, executionLogResponse);
        if (log.getCreateTime() > 0) {
            executionLogResponse.setCreateTime(new Date(log.getCreateTime()));
        }
        if (log.getStartTime() > 0) {
            executionLogResponse.setStartDate(new Date(log.getStartTime()));
        }
        if (log.getEndTime() > 0) {
            executionLogResponse.setEndDate(new Date(log.getEndTime()));
        }
        try {
            executionLogResponse.setMeta(JSON_MAPPER.readValue(log.getMeta(), MAP_REFERENCE));
        } catch (IOException e) {
            executionLogResponse.setMeta(new HashMap<>(0));
        }
        executionLogResponse.setTaskClaim(convertToResponse(log.getTaskClaim()));
        executionLogResponse.setNotes(convertToNoteResponse(log.getNotes()));
        return executionLogResponse;
    }

    /**
     * 执行日志模型集合转响应体集合
     *
     * @param logs 执行日志表模型集合
     * @return List<ExecutionLogResponse>
     */
    public static List<ExecutionLogResponse> convertToLogResponse(List<TaskExecutionLog> logs) {
        if (logs == null || logs.size() == 0) {
            return new ArrayList<>();
        }
        return logs.stream().map(ModelUtils::convertToResponse).collect(Collectors.toList());
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
     * @return List<NoteResponse>
     */
    public static List<NoteResponse> convertToNoteResponse(List<Note> notes) {
        if (notes == null || notes.size() == 0) {
            return new ArrayList<>();
        }
        return notes.stream().map(ModelUtils::convertToResponse).collect(Collectors.toList());
    }

}
