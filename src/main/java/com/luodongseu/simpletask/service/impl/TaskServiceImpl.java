package com.luodongseu.simpletask.service.impl;

import com.luodongseu.simpletask.bean.*;
import com.luodongseu.simpletask.enums.StatusEnum;
import com.luodongseu.simpletask.exception.ErrorCode;
import com.luodongseu.simpletask.exception.GlobalException;
import com.luodongseu.simpletask.model.*;
import com.luodongseu.simpletask.repository.*;
import com.luodongseu.simpletask.service.TaskService;
import com.luodongseu.simpletask.utils.ModelUtils;
import com.luodongseu.simpletask.utils.NoteUtils;
import com.luodongseu.simpletask.utils.ObjectUtils;
import com.luodongseu.simpletask.utils.SpecificationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.util.*;


/**
 * @author luodongseu
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskClaimRepository taskClaimRepository;
    private final TaskExecutionLogRepository taskExecutionLogRepository;
    private final TaskRewardTemplateRepository rewardTemplateRepository;
    private final TaskWhiteListRepository whiteListRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository,
                           TaskClaimRepository taskClaimRepository,
                           TaskExecutionLogRepository taskExecutionLogRepository,
                           TaskRewardTemplateRepository rewardTemplateRepository,
                           TaskWhiteListRepository whiteListRepository) {
        this.taskRepository = taskRepository;
        this.taskClaimRepository = taskClaimRepository;
        this.taskExecutionLogRepository = taskExecutionLogRepository;
        this.rewardTemplateRepository = rewardTemplateRepository;
        this.whiteListRepository = whiteListRepository;
    }

    @Override
    public String createNewRewardTemplate(@NotNull TaskRewardTemplateRequest rewardBody) {
        ObjectUtils.requireNonEmpty(rewardBody.getCreator(), "模板创建者不能为空");
        ObjectUtils.requireNonEmpty(rewardBody.getMeta(), "模板元数据不能为空");

        TaskRewardTemplate newRewardTemplate = new TaskRewardTemplate();
        newRewardTemplate.setId(UUID.randomUUID().toString());
        newRewardTemplate.setCreator(rewardBody.getCreator());
        newRewardTemplate.setDescription(ModelUtils.writeValueAsString(rewardBody.getMeta()));
        newRewardTemplate.setType(rewardBody.getType());
        return rewardTemplateRepository.save(newRewardTemplate).getId();
    }

    @Override
    public Page<TaskRewardTemplateResponse> queryAllRewardTemplate(List<Specification<TaskRewardTemplate>> specifications, Pageable pageable) {
        Page<TaskRewardTemplate> taskRewardTemplates = SpecificationUtils.getPageData(rewardTemplateRepository, specifications, pageable);
        return new PageImpl<>(ModelUtils.convertToRewardResponse(taskRewardTemplates.getContent()),
                pageable, taskRewardTemplates.getTotalElements());
    }

    @Override
    public void saveTask(Task task) {
        ObjectUtils.requireNonEmpty(task, Objects::isNull, "任务不能为空");
        ObjectUtils.requireNonEmpty(task.getId(), "任务ID不能为空");

        this.taskRepository.save(task);
    }

    @Override
    public String createNewTask(TaskRequest task) {
        ObjectUtils.requireNonEmpty(task, Objects::isNull, "任务内容不能为空");
        ObjectUtils.requireNonEmpty(task.getCreator(), "任务创建者不能为空");
        ObjectUtils.requireNonEmpty(task.getDescription(), "任务描述信息不能为空");
        ObjectUtils.requireNonEmpty(task.getStartDate(), Objects::isNull, "任务起始时间不能为空");

        TaskRewardTemplate rewardTemplate = StringUtils.isEmpty(task.getRewardTemplateId()) ?
                null :
                findRewardTemplateById(task.getRewardTemplateId());
        Task newTask = new Task();
        String taskId = UUID.randomUUID().toString();
        newTask.setId(taskId);
        newTask.setStatus(task.getStartDate().getTime() > System.currentTimeMillis() ? StatusEnum.NOT_READY : StatusEnum.IN_PROGRESS);
        newTask.setCreateTime(System.currentTimeMillis());
        newTask.setStartTime(task.getStartDate().getTime());
        newTask.setEndTime(task.getEndDate() != null ? task.getEndDate().getTime() : 0);
        newTask.setRewardTemplate(rewardTemplate);
        newTask.setMeta(ModelUtils.writeValueAsString(task.getMeta()));
        BeanUtils.copyProperties(task, newTask);
        newTask.setNotes(NoteUtils.addNote(newTask.getNotes(), "[System note] New task", taskId));
        taskRepository.save(newTask);
        // 白名单
        addTaskWhiteList(taskId, task.getWhiteList());
        return taskId;
    }

    @Override
    public Page<TaskResponse> queryAllTask(Pageable pageable) {
        if (null == pageable) {
            return new PageImpl<>(ModelUtils.convertToTaskResponse(taskRepository.findAll()));
        }
        Page<Task> allTasks = taskRepository.findAll(pageable);
        return new PageImpl<>(ModelUtils.convertToTaskResponse(allTasks.getContent()), pageable, allTasks.getTotalElements());
    }

    @Override
    public Page<TaskResponse> queryAllTask(List<Specification<Task>> specifications, Pageable pageable) {
        Page<Task> taskPage = SpecificationUtils.getPageData(taskRepository, specifications, pageable);
        if (null != pageable) {
            return new PageImpl<>(
                    ModelUtils.convertToTaskResponse(
                            taskPage.getContent()), pageable, taskPage.getTotalElements());
        }
        return new PageImpl<>(
                ModelUtils.convertToTaskResponse(
                        taskPage.getContent()));
    }

    @Override
    public boolean updateTask(String taskId, TaskRequest task) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");
        ObjectUtils.requireNonEmpty(task, Objects::isNull, "新的任务内容不能为空");

        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            BeanUtils.copyProperties(task, oldTask);
            taskRepository.save(oldTask);
            return true;
        }
        throw new GlobalException(ErrorCode.TASK_NOT_FOUND, "没有找到指定的任务", null);
    }

    @Override
    public boolean startTask(String taskId, String note) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            if (StatusEnum.NOT_READY != oldTask.getStatus()) {
                throw new GlobalException(ErrorCode.INTERNAL_ERROR, "无法开始非未开始状态的任务", taskId, null);
            }
            oldTask.setStatus(StatusEnum.IN_PROGRESS);
            oldTask.setNotes(NoteUtils.addNote(oldTask.getNotes(), note, taskId));
            startTaskClaim(taskId, "[System Note] Task started");
            taskRepository.save(oldTask);
            return true;
        }
        throw new GlobalException(ErrorCode.TASK_NOT_FOUND, "没有找到指定的任务", null);
    }

    @Override
    public boolean cancelTask(String taskId, String note) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            if (Arrays.asList(StatusEnum.CANCELED, StatusEnum.INVALID, StatusEnum.END, StatusEnum.COMPLETE, StatusEnum.DELETED).contains(oldTask.getStatus())) {
                throw new GlobalException(ErrorCode.INTERNAL_ERROR, "无法取消已结束的任务", taskId, null);
            }
            oldTask.setStatus(StatusEnum.CANCELED);
            oldTask.setNotes(NoteUtils.addNote(oldTask.getNotes(), note, taskId));
            cancelTaskClaim(taskId, "[System Note] Task canceled");
            taskRepository.save(oldTask);
            return true;
        }
        throw new GlobalException(ErrorCode.TASK_NOT_FOUND, "没有找到指定的任务", null);
    }

    @Override
    public boolean endTask(String taskId, String note) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            if (Arrays.asList(StatusEnum.CANCELED, StatusEnum.INVALID, StatusEnum.END, StatusEnum.COMPLETE, StatusEnum.DELETED).contains(oldTask.getStatus())) {
                throw new GlobalException(ErrorCode.INTERNAL_ERROR, "无法结束已经结束的任务", taskId, null);
            }
            oldTask.setStatus(StatusEnum.END);
            oldTask.setNotes(NoteUtils.addNote(oldTask.getNotes(), note, taskId));
            cancelTaskClaim(taskId, "[System Note] Task ended");
            taskRepository.save(oldTask);
            return true;
        }
        throw new GlobalException(ErrorCode.TASK_NOT_FOUND, "没有找到指定的任务", null);
    }


    @Override
    public boolean deleteTask(String taskId, String note) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            if (StatusEnum.DELETED == oldTask.getStatus()) {
                throw new GlobalException(ErrorCode.INTERNAL_ERROR, "无法删除已经删除的任务", taskId, null);
            }
            oldTask.setStatus(StatusEnum.DELETED);
            oldTask.setNotes(NoteUtils.addNote(oldTask.getNotes(), note, taskId));
            cancelTaskClaim(taskId, "[System Note] Task deleted");
            taskRepository.save(oldTask);
            return true;
        }
        throw new GlobalException(ErrorCode.TASK_NOT_FOUND, "没有找到指定的任务", null);
    }

    @Override
    public boolean addTaskWhiteList(String taskId, String[] whiteList) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Task oldTask = findTaskById(taskId);
        if (null == oldTask || null == whiteList || whiteList.length == 0) {
            return false;
        }
        List<TaskWhiteList> toSaveWhiteList = new ArrayList<>();
        Arrays.stream(whiteList).forEach(s -> {
            if (!StringUtils.isEmpty(s)) {
                TaskWhiteList newWhiteList = new TaskWhiteList();
                newWhiteList.setId(UUID.randomUUID().toString());
                newWhiteList.setTask(oldTask);
                newWhiteList.setClaimer(s);
                toSaveWhiteList.add(newWhiteList);
            }
        });
        whiteListRepository.saveAll(toSaveWhiteList);
        return true;
    }

    @Override
    public Page<TaskWhiteListResponse> queryAllTaskWhiteList(List<Specification<TaskWhiteList>> specifications, Pageable pageable) {
        Page<TaskWhiteList> whiteLists = SpecificationUtils.getPageData(whiteListRepository, specifications, pageable);
        return new PageImpl<>(
                ModelUtils.convertToWhiteListResponse(whiteLists.getContent()),
                pageable,
                whiteLists.getTotalElements());
    }

    @Override
    public boolean removeTaskWhiteList(String taskId, String whiteMeta) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");
        ObjectUtils.requireNonEmpty(whiteMeta, "白名单名单不能为空");

        Specification<TaskWhiteList> specifications = (Root<TaskWhiteList> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Join<TaskWhiteList, Task> taskJoin = root.join(TaskWhiteList_.task);
            return criteriaBuilder.and(criteriaBuilder.equal(taskJoin.get(Task_.id), taskId),
                    criteriaBuilder.equal(root.get(TaskWhiteList_.claimer), whiteMeta));
        };

        Page<TaskWhiteList> whiteLists = SpecificationUtils.getPageData(whiteListRepository, Collections.singletonList(specifications), null);
        if (whiteLists.getTotalElements() == 0) {
            return false;
        }
        whiteListRepository.deleteAll(whiteLists.getContent());
        return true;
    }

    @Override
    public String claimTask(String taskId, TaskClaimRequest claimRequest) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");
        ObjectUtils.requireNonEmpty(claimRequest.getClaimer(), "认领者不能为空");
        ObjectUtils.requireNonEmpty(claimRequest.getCategory(), Objects::isNull, "认领类别不能为空");

        Task task = findTaskById(taskId);
        if (null == task) {
            throw new GlobalException(ErrorCode.PARAMETER_ERROR, "没有找到指定的任务信息", null);
        }
        TaskClaim newClaim = new TaskClaim();
        String claimId = UUID.randomUUID().toString();
        newClaim.setId(claimId);
        newClaim.setCategory(claimRequest.getCategory());
        newClaim.setClaimer(claimRequest.getClaimer());
        newClaim.setStatus(StatusEnum.NOT_READY);
        newClaim.setTask(task);
        newClaim.setNotes(NoteUtils.addNote(newClaim.getNotes(), "[System note] New claim", claimId));
        return taskClaimRepository.save(newClaim).getId();
    }

    @Override
    public void saveTaskClaim(TaskClaim taskClaim) {
        ObjectUtils.requireNonEmpty(taskClaim, Objects::isNull, "认领内容不能为空");
        ObjectUtils.requireNonEmpty(taskClaim.getId(), "认领ID不能为空");
        ObjectUtils.requireNonEmpty(taskClaim.getClaimer(), "认领者不能为空");

        taskClaimRepository.save(taskClaim);
    }

    @Override
    public boolean startTaskClaim(@NotNull String taskId, String note) {
        Specification<TaskClaim> specification = (Root<TaskClaim> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Join<TaskClaim, Task> taskJoin = root.join(TaskClaim_.task);
            return criteriaBuilder.equal(taskJoin.get(Task_.id), taskId);
        };
        List<TaskClaim> claimList = taskClaimRepository.findAll(specification);
        claimList.forEach(c -> {
            if (StatusEnum.IN_PROGRESS == c.getStatus()) {
                c.setStatus(StatusEnum.IN_PROGRESS);
                c.setNotes(NoteUtils.addNote(c.getNotes(), note, taskId));
            }
        });
        taskClaimRepository.saveAll(claimList);
        return true;
    }

    @Override
    public boolean cancelTaskClaim(@NotNull String taskId, String note) {
        Specification<TaskClaim> specification = (Root<TaskClaim> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Join<TaskClaim, Task> taskJoin = root.join(TaskClaim_.task);
            return criteriaBuilder.equal(taskJoin.get(Task_.id), taskId);
        };
        List<TaskClaim> claimList = taskClaimRepository.findAll(specification);
        claimList.forEach(c -> {
            if (StatusEnum.IN_PROGRESS == c.getStatus()) {
                c.setStatus(StatusEnum.INVALID);
                c.setNotes(NoteUtils.addNote(c.getNotes(), note, taskId));
            }
        });
        taskClaimRepository.saveAll(claimList);
        return true;
    }

    @Override
    public boolean updateClaimProgress(String taskClaimId, Map<String, Object> newMeta, boolean completed) {
        ObjectUtils.requireNonEmpty(taskClaimId, "认领ID不能为空");

        if (newMeta == null) {
            return false;
        }
        TaskClaim oldTaskClaim = findTaskClaimById(taskClaimId);
        if (null != oldTaskClaim) {
            oldTaskClaim.setMeta(ModelUtils.writeValueAsString(newMeta));
            if (completed) {
                oldTaskClaim.setStatus(StatusEnum.COMPLETE);
            }
            taskClaimRepository.save(oldTaskClaim);
            return true;
        }
        return false;
    }

    @Override
    public Page<TaskClaimResponse> queryAllClaims(List<Specification<TaskClaim>> specifications, Pageable pageable) {
        Page<TaskClaim> claims = SpecificationUtils.getPageData(taskClaimRepository, specifications, pageable);
        return new PageImpl<>(
                ModelUtils.convertToClaimResponse(claims.getContent()),
                pageable,
                claims.getTotalElements()
        );
    }

    @Override
    public String addExecutionLog(ExecutionLogRequest logRequestBody) {
        ObjectUtils.requireNonEmpty(logRequestBody, Objects::isNull, "执行内容不能为空");
        ObjectUtils.requireNonEmpty(logRequestBody.getTaskClaimId(), "认领ID不能为空");

        TaskClaim taskClaim = findTaskClaimById(logRequestBody.getTaskClaimId());
        if (null == taskClaim) {
            throw new GlobalException(ErrorCode.PARAMETER_ERROR, "没有找到指定的认领信息", null);
        }

        TaskExecutionLog executionLog = new TaskExecutionLog();
        String executionLogId = UUID.randomUUID().toString();
        executionLog.setId(executionLogId);
        executionLog.setMeta(ModelUtils.writeValueAsString(logRequestBody.getMeta()));
        if (null != logRequestBody.getStartDate()) {
            executionLog.setStartTime(logRequestBody.getStartDate().getTime());
        }
        if (null != logRequestBody.getEndDate()) {
            executionLog.setStartTime(logRequestBody.getEndDate().getTime());
        }
        executionLog.setStatus(logRequestBody.isComplete() ? StatusEnum.COMPLETE : StatusEnum.IN_PROGRESS);
        executionLog.setTaskClaim(taskClaim);
        executionLog.setNotes(NoteUtils.addNote(executionLog.getNotes(), "[System note] New log", executionLogId));
        return null;
    }

    @Override
    public void saveExecutionLog(TaskExecutionLog executionLog) {
        ObjectUtils.requireNonEmpty(executionLog, Objects::isNull, "执行内容不能为空");

        taskExecutionLogRepository.save(executionLog);
    }

    @Override
    public Page<ExecutionLogResponse> queryAllLogs(List<Specification<TaskExecutionLog>> specifications, Pageable pageable) {
        Page<TaskExecutionLog> logs = SpecificationUtils.getPageData(taskExecutionLogRepository, specifications, pageable);
        return new PageImpl<>(
                ModelUtils.convertToLogResponse(logs.getContent()),
                pageable,
                logs.getTotalElements()
        );
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

    /**
     * 通过rewardTemplateId查询TaskRewardTemplate对象
     *
     * @param rewardTemplateId TaskRewardTemplate ID
     * @return TaskRewardTemplate
     */
    private TaskRewardTemplate findRewardTemplateById(String rewardTemplateId) {
        Optional<TaskRewardTemplate> optionalTask = rewardTemplateRepository.findById(rewardTemplateId);
        return optionalTask.orElse(null);
    }
}
