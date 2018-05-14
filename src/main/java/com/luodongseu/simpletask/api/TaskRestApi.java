package com.luodongseu.simpletask.api;

import com.luodongseu.simpletask.bean.TaskRequest;
import com.luodongseu.simpletask.bean.TaskResponse;
import com.luodongseu.simpletask.bean.TaskWhiteListResponse;
import com.luodongseu.simpletask.model.Task;
import com.luodongseu.simpletask.model.TaskWhiteList;
import com.luodongseu.simpletask.model.TaskWhiteList_;
import com.luodongseu.simpletask.model.Task_;
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

    @ApiOperation("条件查询所有的任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "offset", value = "页码值，从0开始", defaultValue = "0", paramType = "query", type = "int", example = "0"),
            @ApiImplicitParam(name = "limit", value = "每页数据量", defaultValue = "10", paramType = "query", type = "int", example = "10"),
            @ApiImplicitParam(name = "creator", value = "创建人", paramType = "query", type = "String", example = "abc")
    })
    @GetMapping
    public Page<TaskResponse> queryAllTask(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                           @RequestParam(value = "limit", defaultValue = "10") int limit,
                                           @RequestParam(value = "creator", required = false) String creator) {
        List<Specification<Task>> querySpecs = new ArrayList<>();
        if (!StringUtils.isEmpty(creator)) {
            querySpecs.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Task_.creator), creator));
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
}
