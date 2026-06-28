package com.taskflow.model;

import java.time.LocalDate;

/**
 * A task tied to professional obligations — projects, meetings, deliverables.
 */
public class WorkTask extends AbstractTask {

    private String project;
    private String department;

    public WorkTask(String title, String description, LocalDate deadline,
                     Priority priority, String project, String department) {
        super(title, description, deadline, priority);
        this.project = project;
        this.department = department;
    }

    public WorkTask(int id, String title, String description, LocalDate deadline,
                     Priority priority, boolean completed, String project, String department) {
        super(id, title, description, deadline, priority, completed);
        this.project = project;
        this.department = department;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getTaskType() {
        return "WORK";
    }

    @Override
    protected String extraFieldsToString() {
        return String.format("Project:     %s%nDepartment:  %s%n", project, department);
    }

    @Override
    protected String extraFieldsToRecord() {
        return escape(project) + "|" + escape(department);
    }
}
