package com.luodongseu.simpletask.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据库条件查询断言帮助类
 *
 * @author luodongseu
 */
public class SpecificationUtils {

    /**
     * 空数据页
     */
    public static Page EMPTY_PAGE = new PageImpl<>(new ArrayList<>());

    /**
     * 根据Spec列表查询数据库，返回页数据
     *
     * @param repository        操作的数据库接口对象
     * @param specificationList 列表
     * @param pageable          页码条件
     * @return Page<T>
     */
    @SuppressWarnings("unchecked")
    public static <T> Page<T> getPageData(Repository repository, List<Specification<T>> specificationList, Pageable pageable) {
        List<Specification<T>> validSpecifications = specificationList != null ?
                specificationList.stream().filter(Objects::nonNull).collect(Collectors.toList()) : null;
        if (validSpecifications == null || validSpecifications.size() == 0) {
            if (repository instanceof JpaRepository) {
                if (null != pageable) {
                    return ((JpaRepository) repository).findAll(pageable);
                } else {
                    return new PageImpl<T>(((JpaRepository) repository).findAll());
                }
            }
            return EMPTY_PAGE;
        }
        if (repository instanceof JpaSpecificationExecutor) {
            Specification<T> specifications = Specification.where(validSpecifications.get(0));
            for (int i = 1; i < validSpecifications.size(); i++) {
                if (null != validSpecifications.get(i)) {
                    specifications = specifications.and(validSpecifications.get(i));
                }
            }
            if (null != pageable) {
                return ((JpaSpecificationExecutor<T>) repository).findAll(specifications, pageable);
            } else {
                return new PageImpl<>(((JpaSpecificationExecutor<T>) repository).findAll(specifications));
            }
        }
        return EMPTY_PAGE;
    }

}
