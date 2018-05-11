package com.luodongseu.simpletask.api;

import com.luodongseu.simpletask.bean.TaskRequest;
import com.luodongseu.simpletask.bean.TaskResponse;
import com.luodongseu.simpletask.model.Task;
import com.luodongseu.simpletask.model.Task_;
import com.luodongseu.simpletask.service.TaskService;
import com.luodongseu.simpletask.utils.ObjectUtils;
import com.luodongseu.simpletask.utils.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

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
    public Page<TaskResponse> findAllTask(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                          @RequestParam(value = "limit", defaultValue = "10") int limit,
                                          @RequestParam(value = "creator", required = false) String creator) {
        List<Specification<Task>> querySpecs = new ArrayList<>();
        if (!StringUtils.isEmpty(creator)) {
            querySpecs.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Task_.creator), creator));
        }
        return taskService.queryAllTask(querySpecs, PageUtils.loadPage(offset, limit));
    }

    @GetMapping("/{taskId}")
    public TaskResponse findAllTask(@PathVariable("taskId") String taskId) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Specification<Task> idSpec = (Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), taskId);
        Page<TaskResponse> tasks = taskService.queryAllTask(Collections.singletonList(idSpec), PageUtils.loadPage(0, 1));
        return tasks.getTotalElements() > 0 ? tasks.getContent().get(0) : null;
    }

}
