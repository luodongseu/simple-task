package com.luodongseu.simpletask.service.impl;

import com.luodongseu.simpletask.bean.TaskResponse;
import com.luodongseu.simpletask.enums.StatusEnum;
import com.luodongseu.simpletask.model.Task;
import com.luodongseu.simpletask.model.Task_;
import com.luodongseu.simpletask.service.TaskCheckerService;
import com.luodongseu.simpletask.service.TaskService;
import com.luodongseu.simpletask.utils.NoteUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luodongseu
 */
@Slf4j
@Service
public class TaskCheckerServiceImpl implements TaskCheckerService {

    private final TaskService taskService;

    @Autowired
    public TaskCheckerServiceImpl(TaskService taskService) {
        this.taskService = taskService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void checkTaskEndTime() {
        log.info(">>>>>> Start to check all expired end time tasks...");
        List<Specification<Task>> specifications = new ArrayList<>();
        Specification<Task> statusSpec = (Root<Task> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder
                criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.equal(root.get(Task_.status), StatusEnum.IN_PROGRESS),
                        criteriaBuilder.equal(root.get(Task_.status), StatusEnum.NOT_READY));
        specifications.add(statusSpec);
        Specification<Task> enTimeSpec = (Root<Task> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder
                criteriaBuilder) -> {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Task_.endTime)));
            return criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get(Task_.endTime), 0L),
                    criteriaBuilder.lessThanOrEqualTo(root.get(Task_.endTime), System.currentTimeMillis()));
        };
        specifications.add(enTimeSpec);
        Page<TaskResponse> notEndTasks = taskService.queryAllTask(specifications, null);
        if (notEndTasks.getTotalElements() >= 1 && notEndTasks.getContent().size() >= 1) {
            notEndTasks.getContent().forEach(task -> taskService.endTask(task.getId(), NoteUtils.getSystemNote("End task")));
        }
        log.info(">>>>>> This time success update {} tasks' status to end.", notEndTasks.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void checkTaskStartTime() {
        log.info(">>>>>> Start to check all expired start time tasks...");
        List<Specification<Task>> specifications = new ArrayList<>();
        Specification<Task> statusSpec = (Root<Task> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder
                criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Task_.status), StatusEnum.NOT_READY);
        specifications.add(statusSpec);
        Specification<Task> enTimeSpec = (Root<Task> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder
                criteriaBuilder) -> {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Task_.startTime)));
            return criteriaBuilder.lessThanOrEqualTo(root.get(Task_.startTime), System.currentTimeMillis());
        };
        specifications.add(enTimeSpec);
        Page<TaskResponse> notStartTasks = taskService.queryAllTask(specifications, null);
        if (notStartTasks.getTotalElements() >= 1 && notStartTasks.getContent().size() >= 1) {
            notStartTasks.getContent().forEach(task -> taskService.startTask(task.getId(), NoteUtils.getSystemNote("Start task")));
        }
        log.info(">>>>>> This time success update {} tasks' status to start.", notStartTasks.getTotalElements());
    }

}
