package com.luodongseu.simpletask.api;


import com.luodongseu.simpletask.bean.TaskRewardTemplateRequest;
import com.luodongseu.simpletask.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping
    public String addNewRewardTemplate(@RequestBody TaskRewardTemplateRequest request) {
        if (null == request.getMeta()) {
            request.setMeta(new HashMap<>(0));
        }
        return taskService.createNewRewardTemplate(request);
    }

}
