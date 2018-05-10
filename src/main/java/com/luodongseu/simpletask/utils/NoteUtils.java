package com.luodongseu.simpletask.utils;

/**
 * 备注帮助工具类
 *
 * @author luodongseu
 */
public class NoteUtils {

    /**
     * 系统备注的前缀
     */
    private static String SYSTEM_NOTE_PREFIX = "[System Note] ";

    /**
     * 获取系统的备注
     *
     * @param note 备注详情
     * @return 带系统前缀的备注信息
     */
    public static String getSystemNote(String note) {
        return SYSTEM_NOTE_PREFIX + (note == null ? "" : note);
    }
}
