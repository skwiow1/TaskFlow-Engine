package com.taskflow.persistence;

import com.taskflow.model.AbstractTask;
import com.taskflow.model.AcademicTask;
import com.taskflow.model.Priority;
import com.taskflow.model.PersonalTask;
import com.taskflow.model.WorkTask;
import com.taskflow.structures.SinglyLinkedList;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles disk persistence for the task list using a simple
 * pipe-delimited line format — one task per line.
 *
 * Chosen over a JSON library deliberately: no external dependency is
 * required, the format is human-readable for debugging, and parsing /
 * writing stays a handful of lines instead of pulling in Jackson/Gson
 * for a single-file CLI project.
 *
 * Record layout per line:
 *   id|TYPE|title|description|deadline(ISO)|PRIORITY|completed|extraField1|extraField2
 */
public class TaskRepository {

    private static final String DELIMITER = "\\|";

    private final Path storageFile;

    public TaskRepository(Path storageFile) {
        this.storageFile = storageFile;
    }

    public void save(SinglyLinkedList<AbstractTask> tasks) {
        List<String> lines = new ArrayList<>();
        for (AbstractTask task : tasks) {
            lines.add(task.toRecordLine());
        }
        try {
            Files.write(storageFile, lines);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save tasks to " + storageFile, e);
        }
    }

    public SinglyLinkedList<AbstractTask> load() {
        SinglyLinkedList<AbstractTask> tasks = new SinglyLinkedList<>();
        if (!Files.exists(storageFile)) {
            return tasks;
        }
        try {
            List<String> lines = Files.readAllLines(storageFile);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                AbstractTask task = parseLine(line);
                if (task != null) {
                    tasks.addLast(task);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load tasks from " + storageFile, e);
        }
        return tasks;
    }

    private AbstractTask parseLine(String line) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < 8) {
            return null;
        }

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String title = parts[2];
        String description = parts[3];
        LocalDate deadline = parts[4].isBlank() ? null : LocalDate.parse(parts[4]);
        Priority priority = Priority.fromString(parts[5]);
        boolean completed = Boolean.parseBoolean(parts[6]);
        String extraA = parts[7];
        String extraB = parts.length > 8 ? parts[8] : "";

        return switch (type) {
            case "WORK" -> new WorkTask(id, title, description, deadline, priority, completed, extraA, extraB);
            case "ACADEMIC" -> new AcademicTask(id, title, description, deadline, priority, completed, extraA, extraB);
            default -> new PersonalTask(id, title, description, deadline, priority, completed, extraA, extraB);
        };
    }
}
