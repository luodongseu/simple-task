package com.luodongseu.simpletask.service.impl;

import com.luodongseu.simpletask.bean.ExecutionLogRequestBody;
import com.luodongseu.simpletask.bean.TaskRequestBody;
import com.luodongseu.simpletask.model.Task;
import com.luodongseu.simpletask.model.TaskClaim;
import com.luodongseu.simpletask.model.TaskExecutionLog;
import com.luodongseu.simpletask.service.TaskService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author luodongseu
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Override
    public void saveTask(Task task) {

    }

    @Override
    public String createNewTask(TaskRequestBody task) {
        return null;
    }

    @Override
    public List<Task> queryAllTask(Pageable pageable) {
        return null;
    }

    @Override
    public List<Task> queryAllTask(List<Specification<Task>> specifications, Pageable pageable) {
        return null;
    }

    @Override
    public boolean updateTask(String taskId, TaskRequestBody task) {
        return false;
    }

    @Override
    public boolean cancelTask(String taskId, String note) {
        return false;
    }

    @Override
    public boolean endTask(String taskId, String note) {
        return false;
    }

    @Override
    public boolean deleteTask(String taskId) {
        return false;
    }

    @Override
    public String claimTask(String taskId, String claimer, String category) {
        return null;
    }

    @Override
    public void saveTaskClaim(TaskClaim taskClaim) {

    }

    @Override
    public boolean updateClaimProgress(String taskClaimId, String newProgress) {
        return false;
    }

    @Override
    public List<TaskClaim> queryAllClaims(List<Specification<TaskClaim>> specifications, Pageable pageable) {
        return null;
    }

    @Override
    public String addExecutionLog(ExecutionLogRequestBody logRequestBody) {
        return null;
    }

    @Override
    public void saveExecutionLog(TaskExecutionLog executionLog) {

    }

    @Override
    public List<TaskExecutionLog> queryAllLogs(List<Specification<TaskExecutionLog>> specifications, Pageable pageable) {
        return null;
    }
}
