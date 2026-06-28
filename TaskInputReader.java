package com.taskflow.model;

import java.time.LocalDate;

/**
 * A task tied to coursework — assignments, exams, readings.
 */
public class AcademicTask extends AbstractTask {

    private String course;
    private String instructor;

    public AcademicTask(String title, String description, LocalDate deadline,
                         Priority priority, String course, String instructor) {
        super(title, description, deadline, priority);
        this.course = course;
        this.instructor = instructor;
    }

    public AcademicTask(int id, String title, String description, LocalDate deadline,
                         Priority priority, boolean completed, String course, String instructor) {
        super(id, title, description, deadline, priority, completed);
        this.course = course;
        this.instructor = instructor;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    @Override
    public String getTaskType() {
        return "ACADEMIC";
    }

    @Override
    protected String extraFieldsToString() {
        return String.format("Course:      %s%nInstructor:  %s%n", course, instructor);
    }

    @Override
    protected String extraFieldsToRecord() {
        return escape(course) + "|" + escape(instructor);
    }
}
