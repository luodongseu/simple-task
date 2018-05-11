package com.luodongseu.simpletask.utils;

import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 对象的检查工具类
 *
 * @author luodongseu
 */
public class ObjectUtils {

    /**
     * 指定的对象空检验
     *
     * @param object    检查对象
     * @param predicate 校验器
     * @param errorMsg  异常信息
     * @param <T>
     * @return 原始对象
     */
    public static <T> T requireNonEmpty(T object, Predicate<T> predicate, String errorMsg) {
        Objects.requireNonNull(object, errorMsg);
        Objects.requireNonNull(predicate, errorMsg);
        if (predicate.test(object)) {
            throw new IllegalArgumentException(errorMsg);
        }
        return object;
    }

    /**
     * 指定的字符串空检验
     *
     * @param str      原始字符串
     * @param errorMsg 错误信息
     * @return 原始值
     */
    public static String requireNonEmpty(String str, String errorMsg) {
        return requireNonEmpty(str, StringUtils::isEmpty, errorMsg);
    }

    /**
     * 指定的集合体空检验
     *
     * @param collection 原始集合
     * @param errorMsg   错误信息
     * @return 原始值
     */
    public static <T> Collection<T> requireNonEmpty(Collection<T> collection, String errorMsg) {
        return requireNonEmpty(collection, (c) -> c == null || c.size() == 0, errorMsg);
    }

    /**
     * 指定的Map体空检验
     *
     * @param collection 原始Map
     * @param errorMsg   错误信息
     * @return 原始值
     */
    public static <T, U> Map<T, U> requireNonEmpty(Map<T, U> collection, String errorMsg) {
        return requireNonEmpty(collection, (c) -> c == null || c.size() == 0, errorMsg);
    }

}
