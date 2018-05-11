package com.luodongseu.simpletask.api;


import com.luodongseu.simpletask.bean.TaskRewardTemplateRequest;
import com.luodongseu.simpletask.bean.TaskRewardTemplateResponse;
import com.luodongseu.simpletask.model.TaskRewardTemplate;
import com.luodongseu.simpletask.service.TaskService;
import com.luodongseu.simpletask.utils.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 对外提供的RewardTemplate所有接口
 *
 * @author luodongseu
 */
@Slf4j
@RestController
@RequestMapping("/api/1.0/reward/template")
public class RewardTemplateRestApi {

    private final TaskService taskService;

    @Autowired
    public RewardTemplateRestApi(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 创建新的奖励模板
     *
     * @param request 模板内容
     * @return 模板ID
     */
    @PostMapping
    public String addNewRewardTemplate(@RequestBody TaskRewardTemplateRequest request) {
        if (null == request.getMeta()) {
            request.setMeta(new HashMap<>(0));
        }
        return taskService.createNewRewardTemplate(request);
    }

    @GetMapping
    public Page<TaskRewardTemplateResponse> queryAllRewardTemplate(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                   @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return taskService.queryAllRewardTemplate(null, PageUtils.loadPage(offset, limit));
    }
}
