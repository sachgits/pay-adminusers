package uk.gov.pay.adminusers.service;

import com.google.common.collect.ImmutableMap;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.Response.Status.*;

public class AdminUsersExceptions {

    public static WebApplicationException undefinedRoleException(String roleName) {
        String error = format("role [%s] not recognised", roleName);
        return buildWebApplicationException(error, BAD_REQUEST.getStatusCode());
    }

    public static WebApplicationException conflictingUsername(String username) {
        String error = format("username [%s] already exists", username);
        return buildWebApplicationException(error, CONFLICT.getStatusCode());
    }

    public static WebApplicationException userLockedException(String username) {
        String error = format("user [%s] locked due to too many login attempts", username);
        return buildWebApplicationException(error, 423); //Locked
    }

    public static WebApplicationException internalServerError(String message) {
        return buildWebApplicationException(message, INTERNAL_SERVER_ERROR.getStatusCode());
    }

    private static WebApplicationException buildWebApplicationException(String error, int status) {
        Map<String, List<String>> errors = ImmutableMap.of("errors", asList(error));
        Response response = Response.status(status)
                .entity(errors)
                .build();
        return new WebApplicationException(response);
    }
}
