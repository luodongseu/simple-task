package com.luodongseu.simpletask.api;

import com.luodongseu.simpletask.bean.TaskRequest;
import com.luodongseu.simpletask.model.Task;
import com.luodongseu.simpletask.service.TaskService;
import com.luodongseu.simpletask.utils.ObjectUtils;
import com.luodongseu.simpletask.utils.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

/**
 * 对外提供的Task相关的所有接口
 *
 * @author luodongseu
 */
@Slf4j
@RestController
@RequestMapping("/api/1.0/task")
public class TaskRestApi {

    private final TaskService taskService;

    @Autowired
    public TaskRestApi(TaskService taskService) {
        this.taskService = taskService;
    }


    /**
     * 创建一个新的任务
     *
     * @return true:创建成功;false:创建失败
     */
    @PostMapping
    public String createOneNewTask(@RequestBody TaskRequest body) {
        log.info("New a task: {}", body);
        if (null == body.getStartDate()) {
            body.setStartDate(new Date());
        }
        return taskService.createNewTask(body);
    }

    @GetMapping
    public Page<Task> findAllTask(@RequestParam("offset") int offset,
                                  @RequestParam("limit") int limit) {
        return taskService.queryAllTask(PageUtils.loadPage(offset, limit));
    }

    @GetMapping("/{taskId}")
    public Task findAllTask(@PathVariable("taskId") String taskId) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Specification<Task> idSpec = (Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), taskId);
        Page<Task> tasks = taskService.queryAllTask(Collections.singletonList(idSpec), PageUtils.loadPage(0, 1));
        if (tasks.getTotalElements() > 0) {
            return tasks.getContent().get(0);
        }
        return null;
    }

}
