package com.luodongseu.simpletask.service;

import com.luodongseu.simpletask.bean.ExecutionLogRequest;
import com.luodongseu.simpletask.bean.TaskRequest;
import com.luodongseu.simpletask.bean.TaskRewardTemplateRequest;
import com.luodongseu.simpletask.enums.ClaimCategoryEnum;
import com.luodongseu.simpletask.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * 任务服务接口
 *
 * @author luodongseu
 */
public interface TaskService {

    /**
     * 创建一个新的奖励模板
     *
     * @param rewardBody 奖励模板信息
     * @return 模板ID
     */
    String createNewRewardTemplate(@NotNull TaskRewardTemplateRequest rewardBody);

    /**
     * 查询所有的奖励模板
     *
     * @param specifications 查询条件
     * @param pageable       页码
     * @return Page<TaskRewardTemplate>
     */
    Page<TaskRewardTemplate> queryAllRewardTemplate(@Nullable List<Specification<TaskRewardTemplate>> specifications, @Nullable Pageable pageable);

    /**
     * 保存Task内容
     *
     * @param task Task对象
     */
    void saveTask(@NotNull Task task);

    /**
     * 创建新的任务
     *
     * @param task 任务信息
     * @return 任务ID
     */
    String createNewTask(@NotNull TaskRequest task);

    /**
     * 查询所有任务
     *
     * @return 全部的任务列表
     */
    Page<Task> queryAllTask(@Nullable Pageable pageable);

    /**
     * 查询所有符合条件的任务
     *
     * @param specifications 搜索条件
     * @param pageable       页码信息
     * @return 符合条件的任务
     */
    Page<Task> queryAllTask(@Nullable List<Specification<Task>> specifications, @Nullable Pageable pageable);

    /**
     * 更新一个任务的信息
     *
     * @param taskId 任务ID
     * @param task   更新后的任务信息
     * @return true:更新成功 false:更新失败
     */
    boolean updateTask(@NotNull String taskId, @NotNull TaskRequest task);

    /**
     * 开始一个任务
     *
     * @param taskId 任务ID
     * @param note   操作备注
     * @return true: 结束成功 false:结束失败
     */
    boolean startTask(@NotNull String taskId, String note);


    /**
     * 取消一个任务
     *
     * @param taskId 任务ID
     * @param note   操作备注
     * @return true: 取消成功 false:取消失败
     */
    boolean cancelTask(@NotNull String taskId, String note);

    /**
     * 结束一个任务
     *
     * @param taskId 任务ID
     * @param note   操作备注
     * @return true: 结束成功 false:结束失败
     */
    boolean endTask(@NotNull String taskId, String note);

    /**
     * 删除指定的任务
     *
     * @param taskId 任务ID
     * @return true:删除成功 false:删除失败
     */
    boolean deleteTask(@NotNull String taskId);


    /**
     * 给指定的任务添加白名单
     *
     * @param taskId    任务ID
     * @param whiteList 白名单
     * @return true:添加成功 false:添加失败
     */
    boolean addTaskWhiteList(@NotNull String taskId, String[] whiteList);

    /**
     * 查询所有的白名单
     *
     * @param specifications 查询条件
     * @param pageable       页码
     * @return Page<TaskWhiteList>
     */
    Page<TaskWhiteList> queryAllTaskWhiteList(@Nullable List<Specification<TaskWhiteList>> specifications, @Nullable Pageable pageable);

    /**
     * 从白名单中移除指定的元数据
     *
     * @param taskId    任务ID
     * @param whiteMeta 元数据
     * @return true:移除成功 false:移除失败
     */
    boolean removeTaskWhiteList(@NotNull String taskId, @NotNull String whiteMeta);

    /**
     * 认领一个任务， 认领后会在任务的备注表中添加相应的认领记录
     *
     * @param taskId   任务ID
     * @param claimer  认领者
     * @param category 认领类别
     * @return 认领的记录ID
     */
    String claimTask(@NotNull String taskId, String claimer, ClaimCategoryEnum category);

    /**
     * 保存一个认领对象
     *
     * @param taskClaim 任务认领对象
     */
    void saveTaskClaim(@NotNull TaskClaim taskClaim);

    /**
     * 更新认领记录的进度
     *
     * @param taskClaimId 认领表的ID
     * @param newProgress 新的进度信息
     * @param completed   是否已完成
     * @return true:更新成功 false:更新失败
     */
    boolean updateClaimProgress(@NotNull String taskClaimId, String newProgress, boolean completed);

    /**
     * 查询指定条件的所有认领记录
     *
     * @param specifications 搜索条件
     * @param pageable       页码信息
     * @return 认领记录列表
     */
    Page<TaskClaim> queryAllClaims(@Nullable List<Specification<TaskClaim>> specifications, @Nullable Pageable pageable);

    /**
     * 添加执行日志
     *
     * @param logRequestBody 日志的请求体
     * @return 日志的ID
     */
    String addExecutionLog(@NotNull ExecutionLogRequest logRequestBody);

    /**
     * 保存一个任务执行日志对象
     *
     * @param executionLog 执行日志对象
     */
    void saveExecutionLog(@NotNull TaskExecutionLog executionLog);

    /**
     * 查询执行条件的所有执行日志
     *
     * @param specifications 搜索条件
     * @param pageable       页码信息
     * @return 执行日志列表
     */
    Page<TaskExecutionLog> queryAllLogs(@Nullable List<Specification<TaskExecutionLog>> specifications, @Nullable Pageable pageable);
}
