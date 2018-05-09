package com.luodongseu.simpletask.service.impl;

import com.luodongseu.simpletask.enums.StatusEnum;
import com.luodongseu.simpletask.model.Note;
import com.luodongseu.simpletask.model.Task;
import com.luodongseu.simpletask.service.EndTaskService;
import com.luodongseu.simpletask.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Service
public class EndTaskServiceImpl implements EndTaskService {

    private final TaskService taskService;

    @Autowired
    public EndTaskServiceImpl(TaskService taskService) {
        this.taskService = taskService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void schedule() {
        Sort sort = Sort.by(Sort.Direction.ASC, "endTime");
        Pageable pageable = PageRequest.of(0, 1, sort);
        List<Specification<Task>> specifications = new ArrayList<>();
        Specification<Task> statusSpec = (Root<Task> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder
                criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.equal(root.get("status"), StatusEnum.IN_PROGRESS),
                        criteriaBuilder.equal(root.get("status"), StatusEnum.NOT_READY));
        specifications.add(statusSpec);
        List<Task> notEndTask = taskService.queryAllTask(specifications, pageable);
        if (notEndTask.size() == 1) {
            Task toEndTask = notEndTask.get(0);
            toEndTask.setStatus(StatusEnum.END);
            toEndTask.addNote(getEndNoteString());
            taskService.saveTask(toEndTask);
        }
    }

    /**
     * 结束任务的备注信息
     *
     * @return 备注信息
     */
    private String getEndNoteString() {
        return "[End Time] System close";
    }

}
