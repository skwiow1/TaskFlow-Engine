package com.taskflow.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Common state and behaviour shared by every concrete task type.
 *
 * Design notes:
 *  - IDs are generated internally via an AtomicInteger rather than trusted
 *    from external input, so persistence/import code can never collide IDs.
 *  - LocalDate replaces raw String deadlines, which gives free validation,
 *    comparison, and "due in N days" arithmetic instead of manual parsing.
 */
public abstract class AbstractTask implements Taskable {

    private static final AtomicInteger ID_SEQUENCE = new AtomicInteger(1);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final int id;
    private String title;
    private String description;
    private LocalDate deadline;
    private Priority priority;
    private boolean completed;

    protected AbstractTask(String title, String description, LocalDate deadline, Priority priority) {
        this.id = ID_SEQUENCE.getAndIncrement();
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.completed = false;
    }

    /**
     * Constructor used when restoring a task from disk, where the ID
     * already exists and must be preserved rather than regenerated.
     */
    protected AbstractTask(int id, String title, String description, LocalDate deadline,
                            Priority priority, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.completed = completed;
        bumpSequenceIfNeeded(id);
    }

    private static void bumpSequenceIfNeeded(int usedId) {
        ID_SEQUENCE.updateAndGet(current -> Math.max(current, usedId + 1));
    }

    public int getId() {
        return id;
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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getFormattedDeadline() {
        return deadline == null ? "N/A" : deadline.format(DATE_FORMAT);
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long daysUntilDeadline() {
        if (deadline == null) {
            return Long.MAX_VALUE;
        }
        return LocalDate.now().until(deadline).getDays();
    }

    /**
     * Subtype-specific fields rendered alongside the common ones.
     * Implemented by each concrete task type.
     */
    protected abstract String extraFieldsToString();

    /**
     * Subtype-specific fields serialized as key=value pairs for persistence.
     */
    protected abstract String extraFieldsToRecord();

    @Override
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append("─────────────────────────────\n");
        sb.append(String.format("#%d  %s%n", id, title));
        sb.append(String.format("Type:        %s%n", getTaskType()));
        sb.append(String.format("Description: %s%n", description));
        sb.append(String.format("Deadline:    %s%n", getFormattedDeadline()));
        sb.append(String.format("Priority:    %s%n", priority.label()));
        sb.append(String.format("Completed:   %s%n", completed ? "Yes" : "No"));
        String extra = extraFieldsToString();
        if (extra != null && !extra.isBlank()) {
            sb.append(extra);
        }
        return sb.toString();
    }

    @Override
    public String toRecordLine() {
        return String.join("|",
                String.valueOf(id),
                getTaskType(),
                escape(title),
                escape(description),
                deadline == null ? "" : deadline.toString(),
                priority.name(),
                String.valueOf(completed),
                extraFieldsToRecord());
    }

    protected static String escape(String value) {
        return value == null ? "" : value.replace("|", "/").replace("\n", " ");
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
