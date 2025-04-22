package com.school.management.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorMessageTemplate {

    CANNOT_DELETE_COURSE_WITH_TEACHERS("Cannot delete course with assigned teachers"), 
    CANNOT_DELETE_COURSE_WITH_STUDENTS("Cannot delete course with enrolled students"),
    ;

    private final String message;

    public String getMessage() {
        return message;
    }

    public String getFormattedMEssage(Object... args) {
        return String.format(message, args);
    }

}
