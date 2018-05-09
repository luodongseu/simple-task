package com.luodongseu.simpletask.service;

import com.luodongseu.simpletask.bean.ExecutionLogRequestBody;
import com.luodongseu.simpletask.bean.TaskRequestBody;
import com.luodongseu.simpletask.model.Task;
import com.luodongseu.simpletask.model.TaskClaim;
import com.luodongseu.simpletask.model.TaskExecutionLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;


/**
 * 任务服务接口
 *
 * @author luodongseu
 */
public interface TaskService {

    /**
     * 保存Task内容
     *
     * @param task Task对象
     */
    void saveTask(Task task);

    /**
     * 创建新的任务
     *
     * @param task 任务信息
     * @return 任务ID
     */
    String createNewTask(TaskRequestBody task);

    /**
     * 查询所有任务
     *
     * @return 全部的任务列表
     */
    List<Task> queryAllTask(Pageable pageable);

    /**
     * 查询所有符合条件的任务
     *
     * @param specifications 搜索条件
     * @param pageable       页码信息
     * @return 符合条件的任务
     */
    List<Task> queryAllTask(List<Specification<Task>> specifications, Pageable pageable);

    /**
     * 更新一个任务的信息
     *
     * @param taskId 任务ID
     * @param task   更新后的任务信息
     * @return true:更新成功 false:更新失败
     */
    boolean updateTask(String taskId, TaskRequestBody task);

    /**
     * 取消一个任务
     *
     * @param taskId 任务ID
     * @param note   操作备注
     * @return true: 取消成功 false:取消失败
     */
    boolean cancelTask(String taskId, String note);

    /**
     * 结束一个任务
     *
     * @param taskId 任务ID
     * @param note   操作备注
     * @return true: 结束成功 false:结束失败
     */
    boolean endTask(String taskId, String note);

    /**
     * 删除指定的任务
     *
     * @param taskId 任务ID
     * @return true:删除成功 false:删除失败
     */
    boolean deleteTask(String taskId);

    /**
     * 认领一个任务， 认领后会在任务的备注表中添加相应的认领记录
     *
     * @param taskId   任务ID
     * @param claimer  认领者
     * @param category 认领类别
     * @return 认领的记录ID
     */
    String claimTask(String taskId, String claimer, String category);

    /**
     * 保存一个认领对象
     *
     * @param taskClaim 任务认领对象
     */
    void saveTaskClaim(TaskClaim taskClaim);

    /**
     * 更新认领记录的进度
     *
     * @param taskClaimId 认领表的ID
     * @param newProgress 新的进度信息
     * @return true:更新成功 false:更新失败
     */
    boolean updateClaimProgress(String taskClaimId, String newProgress);

    /**
     * 查询指定条件的所有认领记录
     *
     * @param specifications 搜索条件
     * @param pageable       页码信息
     * @return 认领记录列表
     */
    List<TaskClaim> queryAllClaims(List<Specification<TaskClaim>> specifications, Pageable pageable);

    /**
     * 添加执行日志
     *
     * @param logRequestBody 日志的请求体
     * @return 日志的ID
     */
    String addExecutionLog(ExecutionLogRequestBody logRequestBody);

    /**
     * 保存一个任务执行日志对象
     *
     * @param executionLog 执行日志对象
     */
    void saveExecutionLog(TaskExecutionLog executionLog);

    /**
     * 查询执行条件的所有执行日志
     *
     * @param specifications 搜索条件
     * @param pageable       页码信息
     * @return 执行日志列表
     */
    List<TaskExecutionLog> queryAllLogs(List<Specification<TaskExecutionLog>> specifications, Pageable pageable);
}
