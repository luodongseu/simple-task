package com.luodongseu.simpletask.repository;

import com.luodongseu.simpletask.model.TaskRewardTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * TaskRewardTemplate模型的数据库操作接口
 * <p>
 * 继承JpaSpecificationExecutor 实现动态查询
 *
 * @author luodong
 */
@Repository
public interface TaskRewardTemplateRepository extends JpaRepository<TaskRewardTemplate, String>, JpaSpecificationExecutor<TaskRewardTemplate> {
}
