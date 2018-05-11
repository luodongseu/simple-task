package com.luodongseu.simpletask.utils;

import com.luodongseu.simpletask.model.Note;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

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

    /**
     * 添加备注
     *
     * @param note      备注对象
     * @param subjectId 属主ID
     */
    public static List<Note> addNote(List<Note> notes, Note note, String subjectId) {
        if (null != note) {
            if (StringUtils.isEmpty(note.getId())) {
                note.setId(UUID.randomUUID().toString());
            }
            note.setSubjectId(subjectId);
            notes.add(note);
        }
        return notes;
    }

    /**
     * 添加备注
     *
     * @param noteString 备注内容
     * @param subjectId  属主ID
     */
    public static List<Note> addNote(List<Note> notes, String noteString, String subjectId) {
        if (!StringUtils.isEmpty(noteString)) {
            Note note = new Note();
            note.setId(UUID.randomUUID().toString());
            note.setNote(noteString);
            note.setSubjectId(subjectId);
            notes.add(note);
        }
        return notes;
    }
}
