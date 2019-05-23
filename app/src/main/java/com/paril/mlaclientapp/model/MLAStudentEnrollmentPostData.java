package com.paril.mlaclientapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by paril.shah on 22-Oct-17.
 */

public class MLAStudentEnrollmentPostData {

    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName("student_id")
    @Expose
    private String studentId;
    @SerializedName("instructor_id")
    @Expose
    private String instructorId;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

}
