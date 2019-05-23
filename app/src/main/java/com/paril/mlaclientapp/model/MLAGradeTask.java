package com.paril.mlaclientapp.model;

import java.io.Serializable;

public class MLAGradeTask implements Serializable{

    public String idTask;
    public String subject_id;
    public String topic;
    public String description;
    public String schedule_startTime;
    public String schedule_endTime;
    public String student_id;
    public String instr_grade;

    public String getIdTask() {
        return idTask;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSchedule_startTime() {
        return schedule_startTime;
    }

    public String getSchedule_endTime() {
        return schedule_endTime;
    }

    public String getStudent_id() {
        return student_id;
    }

    public String getInstr_grade() {
        return instr_grade;
    }


}
   