package com.taskflow.model;

/**
 * Strongly-typed priority levels. Using an enum instead of a raw String
 * eliminates invalid values (typos, casing mismatches) and enables
 * ordinal-based sorting if ever needed.
 */
public enum Priority {
    LOW,
    MEDIUM,
    HIGH;

    /**
     * Parses user input case-insensitively, falling back to MEDIUM
     * for unrecognized values instead of throwing during interactive use.
     */
    public static Priority fromString(String raw) {
        if (raw == null) {
            return MEDIUM;
        }
        try {
            return Priority.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return MEDIUM;
        }
    }

    public String label() {
        return switch (this) {
            case LOW -> "Low";
            case MEDIUM -> "Medium";
            case HIGH -> "High";
        };
    }
}
