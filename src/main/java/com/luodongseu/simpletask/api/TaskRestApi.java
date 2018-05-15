package com.luodongseu.simpletask.api;

import com.luodongseu.simpletask.bean.*;
import com.luodongseu.simpletask.enums.ClaimCategoryEnum;
import com.luodongseu.simpletask.exception.ErrorCode;
import com.luodongseu.simpletask.exception.GlobalException;
import com.luodongseu.simpletask.model.*;
import com.luodongseu.simpletask.service.TaskService;
import com.luodongseu.simpletask.utils.ObjectUtils;
import com.luodongseu.simpletask.utils.PageUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * 对外提供的Task相关的所有接口
 *
 * @author luodongseu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/task")
public class TaskRestApi {

    private final TaskService taskService;

    @Autowired
    public TaskRestApi(TaskService taskService) {
        this.taskService = taskService;
    }


    @ApiOperation("创建一个新的任务")
    @ApiResponses({
            @ApiResponse(code = 200, message = "任务ID")
    })
    @PostMapping
    public String createOneNewTask(@RequestBody TaskRequest body) {
        log.info("New a task: {}", body);
        if (null == body.getStartDate()) {
            body.setStartDate(new Date());
        }
        return taskService.createNewTask(body);
    }

    @ApiOperation("更新一个任务")
    @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, paramType = "path", type = "String")
    @PostMapping("/{taskId}")
    public boolean createOneNewTask(@PathVariable("taskId") String taskId, @RequestBody TaskRequest body) {
        log.info("New a task: {}", body);
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        if (null == body.getStartDate()) {
            body.setStartDate(new Date());
        }
        return taskService.updateTask(taskId, body);
    }

    @ApiOperation("条件查询所有的任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "offset", value = "页码值，从0开始", defaultValue = "0", paramType = "query", type = "int", example = "0"),
            @ApiImplicitParam(name = "limit", value = "每页数据量", defaultValue = "10", paramType = "query", type = "int", example = "10"),
            @ApiImplicitParam(name = "creator", value = "创建人", paramType = "query", type = "String", example = "abc")
    })
    @GetMapping
    public Page<TaskResponse> queryAllTask(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                           @RequestParam(value = "limit", defaultValue = "10") int limit,
                                           @RequestParam(value = "creator", required = false) String creator,
                                           @RequestParam(value = "status", required = false) String status) {
        List<Specification<Task>> querySpecs = new ArrayList<>();
        if (!StringUtils.isEmpty(creator)) {
            querySpecs.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Task_.creator), creator));
        }
        if (!StringUtils.isEmpty(status)) {
            querySpecs.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Task_.status), status));
        }
        return taskService.queryAllTask(querySpecs, PageUtils.loadPage(offset, limit));
    }

    @ApiOperation("获取指定ID的任务的详情")
    @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, paramType = "path", type = "String")
    @GetMapping("/{taskId}")
    public TaskResponse queryTask(@PathVariable("taskId") String taskId) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Specification<Task> idSpec = (Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Task_.id), taskId);
        Page<TaskResponse> tasks = taskService.queryAllTask(Collections.singletonList(idSpec), PageUtils.loadPage(0, 1));
        return tasks.getTotalElements() > 0 ? tasks.getContent().get(0) : null;
    }

    @ApiOperation("将指定ID的任务设置为已开始")
    @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, paramType = "path", type = "String")
    @PostMapping("/{taskId}/start")
    public void startTask(@PathVariable("taskId") String taskId, @RequestBody(required = false) String note) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        boolean startResult = taskService.startTask(taskId,
                StringUtils.isEmpty(note) ? "[System Note] User started" : "[User Start] " + note);
        if (!startResult) {
            throw new GlobalException(ErrorCode.UNKNOWN_ERROR, "Start task " + taskId + " failed!", null);
        }
    }

    @ApiOperation("取消指定ID的任务")
    @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, paramType = "path", type = "String")
    @PostMapping("/{taskId}/cancel")
    public void cancelTask(@PathVariable("taskId") String taskId, @RequestBody(required = false) String note) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        boolean startResult = taskService.cancelTask(taskId,
                StringUtils.isEmpty(note) ? "[System Note] User canceled" : "[User Cancel] " + note);
        if (!startResult) {
            throw new GlobalException(ErrorCode.UNKNOWN_ERROR, "Start task " + taskId + " failed!", null);
        }
    }

    @ApiOperation("将指定ID的任务设置为已结束")
    @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, paramType = "path", type = "String")
    @PostMapping("/{taskId}/end")
    public void endTask(@PathVariable("taskId") String taskId, @RequestBody(required = false) String note) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        boolean startResult = taskService.endTask(taskId,
                StringUtils.isEmpty(note) ? "[System Note] User ended" : "[User End] " + note);
        if (!startResult) {
            throw new GlobalException(ErrorCode.UNKNOWN_ERROR, "Start task " + taskId + " failed!", null);
        }
    }

    @ApiOperation("删除指定ID的任务")
    @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, paramType = "path", type = "String")
    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable("taskId") String taskId, @RequestBody(required = false) String note) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        boolean startResult = taskService.deleteTask(taskId,
                StringUtils.isEmpty(note) ? "[System Note] User deleted" : "[User Delete] " + note);
        if (!startResult) {
            throw new GlobalException(ErrorCode.UNKNOWN_ERROR, "Start task " + taskId + " failed!", null);
        }
    }

    @ApiOperation("获取指定ID的任务的白名单")
    @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, paramType = "path", type = "String")
    @GetMapping("/{taskId}/whitelist")
    public Page<TaskWhiteListResponse> queryAllWhiteList(@PathVariable("taskId") String taskId,
                                                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                         @RequestParam(value = "limit", defaultValue = "10") int limit) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Specification<TaskWhiteList> taskSpec = (Root<TaskWhiteList> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Join<TaskWhiteList, Task> taskJoin = root.join(TaskWhiteList_.task);
            return criteriaBuilder.equal(taskJoin.get(Task_.id), taskId);
        };
        return taskService.queryAllTaskWhiteList(Collections.singletonList(taskSpec), PageUtils.loadPage(offset, limit));
    }

    @ApiOperation("主动认领指定的任务")
    @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, paramType = "path", type = "String")
    @ApiResponse(code = 200, message = "执行记录的ID")
    @PostMapping("/{taskId}/claim/apply")
    public String applyClaimTask(@PathVariable("taskId") String taskId, @RequestBody TaskClaimRequest claimRequest) {
        ObjectUtils.requireNonEmpty(taskId, "认领ID不能为空");
        ObjectUtils.requireNonEmpty(claimRequest, Objects::isNull, "认领信息不能为空");
        ObjectUtils.requireNonEmpty(claimRequest.getClaimer(), "认领者不能为空");

        claimRequest.setCategory(ClaimCategoryEnum.APPLIED);
        return taskService.claimTask(taskId, claimRequest);
    }

    @ApiOperation("获取指定ID的任务的认领者信息")
    @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, paramType = "path", type = "String")
    @GetMapping("/{taskId}/claims")
    public Page<TaskClaimResponse> queryAllClaimList(@PathVariable("taskId") String taskId,
                                                     @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                     @RequestParam(value = "limit", defaultValue = "10") int limit) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Specification<TaskClaim> claimSpec = (Root<TaskClaim> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Join<TaskClaim, Task> taskJoin = root.join(TaskClaim_.task);
            return criteriaBuilder.equal(taskJoin.get(Task_.id), taskId);
        };
        return taskService.queryAllClaims(Collections.singletonList(claimSpec), PageUtils.loadPage(offset, limit));
    }

    @ApiOperation("获取指定ID的任务的执行记录")
    @ApiImplicitParam(name = "taskClaimId", value = "任务认领ID", required = true, paramType = "path", type = "String")
    @GetMapping("/{taskClaimId}/executions")
    public Page<ExecutionLogResponse> queryAllExecutionLogList(@PathVariable("taskClaimId") String taskClaimId,
                                                               @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                               @RequestParam(value = "limit", defaultValue = "10") int limit) {
        ObjectUtils.requireNonEmpty(taskClaimId, "认领ID不能为空");

        Specification<TaskExecutionLog> claimSpec = (Root<TaskExecutionLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Join<TaskExecutionLog, TaskClaim> taskJoin = root.join(TaskExecutionLog_.taskClaim);
            return criteriaBuilder.equal(taskJoin.get(TaskClaim_.id), taskClaimId);
        };
        return taskService.queryAllLogs(Collections.singletonList(claimSpec), PageUtils.loadPage(offset, limit));
    }

    @ApiOperation("添加执行记录")
    @ApiImplicitParam(name = "taskClaimId", value = "任务认领ID", required = true, paramType = "path", type = "String")
    @ApiResponse(code = 200, message = "执行记录的ID")
    @PostMapping("/claim/{taskClaimId}/execution")
    public String executeTask(@PathVariable("taskClaimId") String taskClaimId,
                              @RequestBody ExecutionLogRequest logRequest) {
        ObjectUtils.requireNonEmpty(taskClaimId, "认领ID不能为空");
        ObjectUtils.requireNonEmpty(logRequest, Objects::isNull, "认领信息不能为空");

        return taskService.addExecutionLog(logRequest);
    }

}
