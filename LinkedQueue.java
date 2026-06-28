package com.taskflow.model;

/**
 * Contract implemented by every task variant in the system.
 * Decouples consumers (UI, persistence, search) from concrete task types,
 * allowing new task categories to be introduced without touching existing code.
 */
public interface Taskable {

    String getTaskType();

    String toDisplayString();

    String toRecordLine();
}
