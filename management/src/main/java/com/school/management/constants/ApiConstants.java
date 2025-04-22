package com.school.management.constants;

import lombok.NoArgsConstructor;

/**
 * This class contains constants for API endpoints used in the school management
 * system.
 * It defines the base URL and specific endpoints for various resources.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ApiConstants {
    public static final String API_VERSION = "v1";
    public static final String BASE_URL = "/api/" + API_VERSION;
    public static final String STUDENTS_URL = BASE_URL + "/students";
    public static final String TEACHERS_URL = BASE_URL + "/teachers";
    public static final String COURSES_URL = BASE_URL + "/courses";

}
