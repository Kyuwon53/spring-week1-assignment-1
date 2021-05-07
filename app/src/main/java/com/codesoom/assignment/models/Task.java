package com.codesoom.assignment.models;

/**
 * 할 일
 *
 * @author DevRunner21
 * @version 1.0
 * @since 2021.05.07
 */
public class Task {

    private Long id; // 할 일 Id

    private String title; // 할 일 제목

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

}
