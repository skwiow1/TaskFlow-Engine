package com.taskflow.app;

import com.taskflow.model.AbstractTask;
import com.taskflow.model.AcademicTask;
import com.taskflow.model.PersonalTask;
import com.taskflow.model.Priority;
import com.taskflow.model.WorkTask;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Wraps a Scanner to collect and validate the fields needed to build a
 * new task interactively, including dispatch to the correct task subtype.
 */
public class TaskInputReader {

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Scanner scanner;

    public TaskInputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public AbstractTask readNewTask() {
        System.out.print("Task type (1=Personal, 2=Work, 3=Academic): ");
        String typeChoice = scanner.nextLine().trim();

        System.out.print("Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        LocalDate deadline = readDeadline();
        Priority priority = readPriority();

        AbstractTask task = switch (typeChoice) {
            case "2" -> {
                System.out.print("Project: ");
                String project = scanner.nextLine().trim();
                System.out.print("Department: ");
                String department = scanner.nextLine().trim();
                yield new WorkTask(title, description, deadline, priority, project, department);
            }
            case "3" -> {
                System.out.print("Course: ");
                String course = scanner.nextLine().trim();
                System.out.print("Instructor: ");
                String instructor = scanner.nextLine().trim();
                yield new AcademicTask(title, description, deadline, priority, course, instructor);
            }
            default -> {
                System.out.print("Category: ");
                String category = scanner.nextLine().trim();
                System.out.print("Location: ");
                String location = scanner.nextLine().trim();
                yield new PersonalTask(title, description, deadline, priority, category, location);
            }
        };

        System.out.print("Already completed? (yes/no): ");
        boolean completed = scanner.nextLine().trim().equalsIgnoreCase("yes");
        task.setCompleted(completed);

        return task;
    }

    private LocalDate readDeadline() {
        while (true) {
            System.out.print("Deadline (dd/MM/yyyy): ");
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(raw, INPUT_FORMAT);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid date format, please use dd/MM/yyyy.");
            }
        }
    }

    private Priority readPriority() {
        System.out.print("Priority (High/Medium/Low): ");
        String raw = scanner.nextLine().trim();
        return Priority.fromString(raw);
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
