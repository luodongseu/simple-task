package com.luodongseu.simpletask.model;

import org.springframework.util.StringUtils;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * 包含备注的基本类
 *
 * @author luodongseu
 */
public class NoteBase {

    @Id
    private String id;

    /**
     * 备注信息, 以 subject_id 作为外键
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private List<Note> notes = new ArrayList<>();

    /**
     * 添加备注
     *
     * @param note 备注对象
     */
    public void addNote(Note note) {
        if (null != note) {
            note.setSubjectId(this.id);
            this.notes.add(note);
        }
    }

    /**
     * 添加备注
     *
     * @param noteString 备注内容
     */
    public void addNote(String noteString) {
        if (!StringUtils.isEmpty(noteString)) {
            Note note = new Note();
            note.setNote(noteString);
            note.setSubjectId(this.id);
            this.notes.add(note);
        }
    }
}