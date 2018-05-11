package com.luodongseu.simpletask.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luodongseu.simpletask.bean.ExecutionLogRequest;
import com.luodongseu.simpletask.bean.TaskRequest;
import com.luodongseu.simpletask.bean.TaskRewardTemplateRequest;
import com.luodongseu.simpletask.enums.ClaimCategoryEnum;
import com.luodongseu.simpletask.enums.StatusEnum;
import com.luodongseu.simpletask.exception.ErrorCode;
import com.luodongseu.simpletask.exception.GlobalException;
import com.luodongseu.simpletask.model.*;
import com.luodongseu.simpletask.repository.*;
import com.luodongseu.simpletask.service.TaskService;
import com.luodongseu.simpletask.utils.NoteUtils;
import com.luodongseu.simpletask.utils.ObjectUtils;
import com.luodongseu.simpletask.utils.SpecificationUtils;
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
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.util.*;


/**
 * @author luodongseu
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class TaskServiceImpl implements TaskService {

    private static ObjectMapper JSON_MAPPER = new ObjectMapper();

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
        try {
            newRewardTemplate.setDescription(JSON_MAPPER.writeValueAsString(rewardBody.getMeta()));
        } catch (JsonProcessingException e) {
            throw new GlobalException(ErrorCode.INTERNAL_ERROR, "解析Json数据异常", e);
        }
        newRewardTemplate.setType(rewardBody.getType());
        return rewardTemplateRepository.save(newRewardTemplate).getId();
    }

    @Override
    public Page<TaskRewardTemplate> queryAllRewardTemplate(List<Specification<TaskRewardTemplate>> specifications, Pageable pageable) {
        return SpecificationUtils.getPageData(rewardTemplateRepository, specifications, pageable);
    }

    @Override
    public void saveTask(Task task) {
        assert !StringUtils.isEmpty(task.getId());
        this.taskRepository.save(task);
    }

    @Override
    public String createNewTask(TaskRequest task) {
        ObjectUtils.requireNonEmpty(task, Objects::isNull, "任务内容不能为空");
        ObjectUtils.requireNonEmpty(task.getCreator(), "任务创建者不能为空");
        if (StringUtils.isEmpty(task.getDescription()) && StringUtils.isEmpty(task.getDetail())) {
            ObjectUtils.requireNonEmpty(task.getDescription(), "任务描述信息不能为空");
        }

        TaskRewardTemplate rewardTemplate = StringUtils.isEmpty(task.getRewardTemplateId()) ?
                null :
                findRewardTemplateById(task.getRewardTemplateId());
        Task newTask = new Task();
        String taskId = UUID.randomUUID().toString();
        newTask.setId(taskId);
        newTask.setStatus(Objects.requireNonNull(task.getStartDate()).getTime() > System.currentTimeMillis() ? StatusEnum.NOT_READY : StatusEnum.IN_PROGRESS);
        newTask.setCreateTime(System.currentTimeMillis());
        newTask.setStartTime(task.getStartDate().getTime());
        newTask.setEndTime(task.getEndDate() != null ? task.getEndDate().getTime() : 0);
        newTask.setRewardTemplate(rewardTemplate);
        BeanUtils.copyProperties(task, newTask);
        newTask.setNotes(NoteUtils.addNote(newTask.getNotes(), "[System note] New task", taskId));
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
    public boolean updateTask(String taskId, TaskRequest task) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");
        ObjectUtils.requireNonEmpty(task, Objects::isNull, "新的任务内容不能为空");

        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            BeanUtils.copyProperties(task, oldTask);
            taskRepository.save(oldTask);
            return true;
        }
        return false;
    }

    @Override
    public boolean startTask(String taskId, String note) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            oldTask.setStatus(StatusEnum.IN_PROGRESS);
            oldTask.setNotes(NoteUtils.addNote(oldTask.getNotes(), note, taskId));
            taskRepository.save(oldTask);
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelTask(String taskId, String note) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            oldTask.setStatus(StatusEnum.CANCELED);
            oldTask.setNotes(NoteUtils.addNote(oldTask.getNotes(), note, taskId));
            taskRepository.save(oldTask);
            return true;
        }
        return false;
    }

    @Override
    public boolean endTask(String taskId, String note) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            oldTask.setStatus(StatusEnum.END);
            oldTask.setNotes(NoteUtils.addNote(oldTask.getNotes(), note, taskId));
            taskRepository.save(oldTask);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteTask(String taskId) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");

        Task oldTask = findTaskById(taskId);
        if (null != oldTask) {
            taskRepository.delete(oldTask);
        }
        return true;
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
    public Page<TaskWhiteList> queryAllTaskWhiteList(List<Specification<TaskWhiteList>> specifications, Pageable pageable) {
        return SpecificationUtils.getPageData(whiteListRepository, specifications, pageable);
    }

    @Override
    public boolean removeTaskWhiteList(String taskId, String whiteMeta) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");
        ObjectUtils.requireNonEmpty(whiteMeta, "白名单名单不能为空");

        Specification<TaskWhiteList> specifications = (Root<TaskWhiteList> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.equal(root.get(TaskWhiteList_.taskId), taskId),
                        criteriaBuilder.equal(root.get(TaskWhiteList_.claimer), whiteMeta));
        Page<TaskWhiteList> whiteLists = SpecificationUtils.getPageData(whiteListRepository, Collections.singletonList(specifications), null);
        if (whiteLists.getTotalElements() == 0) {
            return false;
        }
        whiteListRepository.deleteAll(whiteLists.getContent());
        return true;
    }

    @Override
    public String claimTask(String taskId, String claimer, ClaimCategoryEnum category) {
        ObjectUtils.requireNonEmpty(taskId, "任务ID不能为空");
        ObjectUtils.requireNonEmpty(claimer, "认领者不能为空");
        ObjectUtils.requireNonEmpty(category, Objects::isNull, "认领者不能为空");

        Task task = findTaskById(taskId);
        if (null == task) {
            throw new GlobalException(ErrorCode.PARAMETER_ERROR, "没有找到指定的任务信息", null);
        }
        TaskClaim newClaim = new TaskClaim();
        String claimId = UUID.randomUUID().toString();
        newClaim.setId(claimId);
        newClaim.setCategory(category);
        newClaim.setClaimer(claimer);
        newClaim.setStatus(StatusEnum.IN_PROGRESS);
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
    public boolean updateClaimProgress(String taskClaimId, String newProgress, boolean completed) {
        ObjectUtils.requireNonEmpty(taskClaimId, "认领ID不能为空");

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
        executionLog.setLog(logRequestBody.getLog());
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
