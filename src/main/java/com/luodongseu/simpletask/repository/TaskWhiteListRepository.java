package com.luodongseu.simpletask.repository;

import com.luodongseu.simpletask.model.TaskWhiteList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * TaskWhiteList模型的数据库操作接口
 * <p>
 * 继承JpaSpecificationExecutor 实现动态查询
 *
 * @author luodong
 */
@Repository
public interface TaskWhiteListRepository extends JpaRepository<TaskWhiteList, String>, JpaSpecificationExecutor<TaskWhiteList> {
}
