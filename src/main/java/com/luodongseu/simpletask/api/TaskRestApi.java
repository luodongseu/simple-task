package com.luodongseu.simpletask.api;

import com.luodongseu.simpletask.bean.TaskRequestBody;
import com.luodongseu.simpletask.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;

/**
 * 对外提供的所有接口
 *
 * @author luodongseu
 */
@RestController
@RequestMapping("/api/1.0")
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
    @PostMapping("/task")
    public String createOneNewTask(@RequestBody TaskRequestBody body) {
        Objects.requireNonNull(body.getCreator(), "创建者的信息不能为空");
        if (null == body.getStartDate()) {
            body.setStartDate(new Date());
        }
        return taskService.createNewTask(body);
    }


}
