package com.luodongseu.simpletask.api;


import com.luodongseu.simpletask.bean.TaskRewardTemplateRequest;
import com.luodongseu.simpletask.bean.TaskRewardTemplateResponse;
import com.luodongseu.simpletask.model.TaskRewardTemplate;
import com.luodongseu.simpletask.model.TaskRewardTemplate_;
import com.luodongseu.simpletask.service.TaskService;
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
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 对外提供的RewardTemplate所有接口
 *
 * @author luodongseu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/reward/template")
public class RewardTemplateRestApi {

    private final TaskService taskService;

    @Autowired
    public RewardTemplateRestApi(TaskService taskService) {
        this.taskService = taskService;
    }

    @ApiOperation("创建新的奖励模板")
    @ApiResponses({
            @ApiResponse(code = 200, message = "模板ID")
    })
    @PostMapping
    public String addNewRewardTemplate(@RequestBody TaskRewardTemplateRequest request) {
        if (null == request.getMeta()) {
            request.setMeta(new HashMap<>(0));
        }
        return taskService.createNewRewardTemplate(request);
    }

    @ApiOperation("查询所有的奖励模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "offset", value = "页码值，从0开始", defaultValue = "0", paramType = "query", type = "int", example = "0"),
            @ApiImplicitParam(name = "limit", value = "每页数据量", defaultValue = "10", paramType = "query", type = "int", example = "10"),
            @ApiImplicitParam(name = "creator", value = "创建人", paramType = "query", type = "String", example = "abc")
    })
    @GetMapping
    public Page<TaskRewardTemplateResponse> queryAllRewardTemplate(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                   @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                                   @RequestParam(value = "creator", required = false) int creator) {
        List<Specification<TaskRewardTemplate>> querySpecs = new ArrayList<>();
        if (!StringUtils.isEmpty(creator)) {
            querySpecs.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(TaskRewardTemplate_.creator), creator));
        }
        return taskService.queryAllRewardTemplate(querySpecs, PageUtils.loadPage(offset, limit));
    }

    @ApiOperation("获取指定ID的模板")
    @ApiImplicitParam(name = "templateId", value = "模板ID", paramType = "path", required = true, type = "String", example = "123")
    @GetMapping("/{templateId}")
    public TaskRewardTemplateResponse queryRewardTemplate(@PathVariable("templateId") String templateId) {
        Specification<TaskRewardTemplate> idSpec = (Root<TaskRewardTemplate> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get(TaskRewardTemplate_.id), templateId);
        Page<TaskRewardTemplateResponse> templates = taskService.queryAllRewardTemplate(Collections.singletonList(idSpec),
                PageUtils.loadPage(0, 1));
        return templates.getTotalElements() > 0 ? templates.getContent().get(0) : null;
    }
}
