package com.luodongseu.simpletask.repository;

import com.luodongseu.simpletask.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Task模型的数据库操作接口
 * <p>
 * 继承JpaSpecificationExecutor 实现动态查询
 *
 * @author luodong
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, String>, JpaSpecificationExecutor<Task> {
}
