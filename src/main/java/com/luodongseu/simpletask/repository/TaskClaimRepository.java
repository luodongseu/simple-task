package com.luodongseu.simpletask.repository;

import com.luodongseu.simpletask.model.TaskClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * TaskClaim模型的数据库操作接口
 * <p>
 * 继承JpaSpecificationExecutor 实现动态查询
 *
 * @author luodong
 */
@Repository
public interface TaskClaimRepository extends JpaRepository<TaskClaim, String>, JpaSpecificationExecutor<TaskClaim> {
}
