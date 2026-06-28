package com.taskflow.model;

import java.time.LocalDate;

/**
 * A task belonging to the user's personal life (errands, appointments,
 * fitness, etc.) as opposed to academic or work-related obligations.
 */
public class PersonalTask extends AbstractTask {

    private String category;
    private String location;

    public PersonalTask(String title, String description, LocalDate deadline,
                         Priority priority, String category, String location) {
        super(title, description, deadline, priority);
        this.category = category;
        this.location = location;
    }

    public PersonalTask(int id, String title, String description, LocalDate deadline,
                         Priority priority, boolean completed, String category, String location) {
        super(id, title, description, deadline, priority, completed);
        this.category = category;
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getTaskType() {
        return "PERSONAL";
    }

    @Override
    protected String extraFieldsToString() {
        return String.format("Category:    %s%nLocation:    %s%n", category, location);
    }

    @Override
    protected String extraFieldsToRecord() {
        return escape(category) + "|" + escape(location);
    }
}
