package com.paril.mlaclientapp.model;

public class MLATaskDetails
    {
        public int idTask;
        public String subject_id;
        public String instructor_id;
        public String topic;
        public String description;
        public boolean isProgressRunning;
        public int lastProgress;
        public String schedule_startTime;
        public String schedule_endTime;

        public boolean isProgressRunning() {
            return isProgressRunning;
        }

        public int getLastProgress() {
            return lastProgress;
        }

        public void setLastProgress(int lastProgress) {
            this.lastProgress = lastProgress;
        }

        public int getIdTask() {
            return idTask;
        }

        public void setIdTask(int idTask) {
            this.idTask = idTask;
        }

        public String getSubject_id() {
            return subject_id;
        }

        public void setSubject_id(String subject_id) {
            this.subject_id = subject_id;
        }

        public String getInstructor_id() {
            return instructor_id;
        }

        public void setInstructor_id(String instructor_id) {
            this.instructor_id = instructor_id;
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

    }