package com.luodongseu.simpletask.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * 页操作的工具类
 *
 * @author luodongseu
 */
public class PageUtils {

    /**
     * 创建页码请求对象
     *
     * @param offset 页码 0开始
     * @param limit  每页大小 1开始
     * @return Pageable
     */
    public static Pageable loadPage(int offset, int limit) {
        if (offset < 0) {
            offset = 0;
        }
        if (limit < 1) {
            offset = 1;
        }
        return PageRequest.of(offset, limit);
    }
}
