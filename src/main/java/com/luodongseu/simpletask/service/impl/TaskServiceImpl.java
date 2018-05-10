package com.luodongseu.simpletask.service.impl;

import com.luodongseu.simpletask.bean.ExecutionLogRequestBody;
import com.luodongseu.simpletask.bean.TaskRequestBody;
import com.luodongseu.simpletask.enums.ClaimCategoryEnum;
import com.luodongseu.simpletask.enums.StatusEnum;
import com.luodongseu.simpletask.model.Task;
import com.luodongseu.simpletask.model.TaskClaim;
import com.luodongseu.simpletask.model.TaskExecutionLog;
import com.luodongseu.simpletask.repository.TaskClaimRepository;
import com.luodongseu.simpletask.repository.TaskExecutionLogRepository;
import com.luodongseu.simpletask.repository.TaskRepository;
import com.luodongseu.simpletask.service.TaskService;
import com.luodongseu.simpletask.utils.SpecificationUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


/**
 * @author luodongseu
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskClaimRepository taskClaimRepository;
    private final TaskExecutionLogRepository taskExecutionLogRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, TaskClaimRepository taskClaimRepository, TaskExecutionLogRepository taskExecutionLogRepository) {
        this.taskRepository = taskRepository;
        this.taskClaimRepository = taskClaimRepository;
        this.taskExecutionLogRepository = taskExecutionLogRepository;
    }

    @Override
    public void saveTask(Task task) {
        assert !StringUtils.isEmpty(task.getId());
        this.taskRepository.save(task);
    }

    @Override
    public String createNewTask(TaskRequestBody task) {
        Objects.requireNonNull(task.getCreator(), "任务创建者不能为空");
        assert !StringUtils.isEmpty(task.getDescription()) || !StringUtils.isEmpty(task.getDetail());

        Assert.hasText(task.getCreator(), "任务创建者不能为空");

        Task newTask = new Task();
        String taskId = UUID.randomUUID().toString();
        newTask.setId(taskId);
        newTask.setStatus(Objects.requireNonNull(task.getStartDate()).getTime() > System.currentTimeMillis() ? StatusEnum.NOT_READY : StatusEnum.IN_PROGRESS);
        newTask.setCreateTime(System.currentTimeMillis());
        BeanUtils.copyProperties(task, newTask);
        newTask.addNote("[System note] New task");
        taskRepository.save(newTask);
        return taskId;
    }

    @Override
    public Page<Task> queryAllTask(Pageable pageable) {
        if (null == pageable) {
            List<Task> allTasks = taskRepository.findAll();
            return new PageImpl<>(allTasks);
        }
        assert pageable.getOffset() >= 0;
        assert pageable.getPageSize() > 0;
        return taskRepository.findAll(pageable);
    }

    @Override
    public Page<Task> queryAllTask(List<Specification<Task>> specifications, Pageable pageable) {
        assert specifications != null;
        return SpecificationUtils.getPageData(taskRepository, specifications, pageable);
    }

    @Override
    public boolean updateTask(String taskId, TaskRequestBody task) {
        assert !StringUtils.isEmpty(taskId);
        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            BeanUtils.copyProperties(task, oldTask);
            taskRepository.save(oldTask);
            return true;
        }
        return false;
    }

    @Override
    public boolean startTask(@NotNull String taskId, String note) {
        assert !StringUtils.isEmpty(taskId);
        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            oldTask.setStatus(StatusEnum.IN_PROGRESS);
            oldTask.addNote(note);
            taskRepository.save(oldTask);
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelTask(String taskId, String note) {
        assert !StringUtils.isEmpty(taskId);
        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            oldTask.setStatus(StatusEnum.CANCELED);
            oldTask.addNote(note);
            taskRepository.save(oldTask);
            return true;
        }
        return false;
    }

    @Override
    public boolean endTask(String taskId, String note) {
        assert !StringUtils.isEmpty(taskId);
        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            oldTask.setStatus(StatusEnum.END);
            oldTask.addNote(note);
            taskRepository.save(oldTask);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteTask(String taskId) {
        assert !StringUtils.isEmpty(taskId);
        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            taskRepository.delete(oldTask);
        }
        return true;
    }

    @Override
    public String claimTask(String taskId, String claimer, ClaimCategoryEnum category) {
        assert !StringUtils.isEmpty(taskId);
        assert !StringUtils.isEmpty(claimer);
        assert null != category;
        Task task = findTaskById(taskId);
        assert null != task;
        TaskClaim newClaim = new TaskClaim();
        String claimId = UUID.randomUUID().toString();
        newClaim.setId(claimId);
        newClaim.setCategory(category);
        newClaim.setClaimer(claimer);
        newClaim.setStatus(StatusEnum.IN_PROGRESS);
        newClaim.setTask(task);
        newClaim.addNote("[System note] New claim");
        taskClaimRepository.save(newClaim);
        return claimId;
    }

    @Override
    public void saveTaskClaim(TaskClaim taskClaim) {
        assert null != taskClaim;
        assert !StringUtils.isEmpty(taskClaim.getId());
        taskClaimRepository.save(taskClaim);
    }

    @Override
    public boolean updateClaimProgress(String taskClaimId, String newProgress, boolean completed) {
        assert !StringUtils.isEmpty(taskClaimId);
        if (newProgress == null) {
            newProgress = "";
        }
        TaskClaim oldTaskClaim = findTaskClaimById(taskClaimId);
        if (null != oldTaskClaim) {
            oldTaskClaim.setProgress(newProgress);
            if (completed) {
                oldTaskClaim.setStatus(StatusEnum.COMPLETE);
            }
            taskClaimRepository.save(oldTaskClaim);
            return true;
        }
        return false;
    }

    @Override
    public Page<TaskClaim> queryAllClaims(List<Specification<TaskClaim>> specifications, Pageable pageable) {
        return SpecificationUtils.getPageData(taskClaimRepository, specifications, pageable);
    }

    @Override
    public String addExecutionLog(ExecutionLogRequestBody logRequestBody) {
        assert null != logRequestBody;
        assert !StringUtils.isEmpty(logRequestBody.getTaskClaimId());
        TaskClaim taskClaim = findTaskClaimById(logRequestBody.getTaskClaimId());
        assert null != taskClaim;

        TaskExecutionLog executionLog = new TaskExecutionLog();
        String id = UUID.randomUUID().toString();
        executionLog.setId(id);
        executionLog.setLog(logRequestBody.getLog());
        if (null != logRequestBody.getStartDate()) {
            executionLog.setStartTime(logRequestBody.getStartDate().getTime());
        }
        if (null != logRequestBody.getEndDate()) {
            executionLog.setStartTime(logRequestBody.getEndDate().getTime());
        }
        executionLog.setStatus(logRequestBody.isComplete() ? StatusEnum.COMPLETE : StatusEnum.IN_PROGRESS);
        executionLog.setTaskClaim(taskClaim);
        executionLog.addNote("[System note] New log");
        return null;
    }

    @Override
    public void saveExecutionLog(TaskExecutionLog executionLog) {
        assert null != executionLog;
        taskExecutionLogRepository.save(executionLog);
    }

    @Override
    public Page<TaskExecutionLog> queryAllLogs(List<Specification<TaskExecutionLog>> specifications, Pageable pageable) {
        return SpecificationUtils.getPageData(taskExecutionLogRepository, specifications, pageable);
    }

    /**
     * 通过TaskId查询Task对象
     *
     * @param taskId Task ID
     * @return Task
     */
    private Task findTaskById(String taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        return optionalTask.orElse(null);
    }

    /**
     * 通过TaskClaimId查询TaskClaim对象
     *
     * @param taskClaimId TaskClaim ID
     * @return TaskClaim
     */
    private TaskClaim findTaskClaimById(String taskClaimId) {
        Optional<TaskClaim> optionalTask = taskClaimRepository.findById(taskClaimId);
        return optionalTask.orElse(null);
    }
}
