package com.paril.mlaclientapp.model;

import java.io.Serializable;

public class MLASubjectDetails implements Serializable
    {
        public String idSubject;
        public String title;
        public String description;
        public String videoEnabled;
        public String audioEnabled;
        public String startDate;
        public String endDate;
        public String idInstructor;
        public String startTime;
        public String endTime;
        public String mailingAlias;
        public int duration;

        public String getIdSubject() {
            return idSubject;
        }

        public void setIdSubject(String idSubject) {
            this.idSubject = idSubject;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIdInstructor() {
            return idInstructor;
        }

        public String getMailingAlias() {
            return mailingAlias;
        }


    }
