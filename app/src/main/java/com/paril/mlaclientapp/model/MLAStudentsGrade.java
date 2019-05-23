package com.paril.mlaclientapp.model;

/**
 * Created by paril on 7/23/2017.
 */
public class MLAStudentsGrade {
    String taskId;
    String topic;
    String grade;

    public MLAStudentsGrade(String taskId, String topic, String grade) {
        this.taskId = taskId;
        this.topic = topic;
        this.grade = grade;
    }
    
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
