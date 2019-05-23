package com.paril.mlaclientapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MLAScheduleDetailPostData {

    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName("instructor_id")
    @Expose
    private String instructorId;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("schedule_startTime")
    @Expose
    private String scheduleStartTime;
    @SerializedName("schedule_endTime")
    @Expose
    private String scheduleEndTime;
    @SerializedName("isQuiz")
    @Expose
    private String isQuiz;
    @SerializedName("repeatTask")
    @Expose
    private String repeatTask;


    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
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

    public String getScheduleStartTime() {
        return scheduleStartTime;
    }

    public void setScheduleStartTime(String scheduleStartTime) {
        this.scheduleStartTime = scheduleStartTime;
    }

    public String getScheduleEndTime() {
        return scheduleEndTime;
    }

    public void setScheduleEndTime(String scheduleEndTime) {
        this.scheduleEndTime = scheduleEndTime;
    }

    public String getIsQuiz() {
        return isQuiz;
    }

    public void setIsQuiz(String isQuiz) {
        this.isQuiz = isQuiz;
    }

    public String getRepeatTask() {
        return repeatTask;
    }

    public void setRepeatTask(String repeatTask) {
        this.repeatTask = repeatTask;
    }

}