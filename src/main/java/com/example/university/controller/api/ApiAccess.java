package com.example.university.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

/**
 * "Is this record yours?" checks. @PreAuthorize checks the ROLE; these check OWNERSHIP.
 * Failing a check gives HTTP 403.
 */
final class ApiAccess {

    private ApiAccess() {
    }

    static boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    /** Admins can access any record; everyone else only the record that belongs to their own login. */
    static void requireAdminOrOwner(Authentication auth, String ownerUsername) {
        if (!hasRole(auth, "ADMIN") && !auth.getName().equals(ownerUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own record.");
        }
    }

    /** Admins and teachers can read any student; a student only their own record. */
    static void requireStaffOrOwner(Authentication auth, String ownerUsername) {
        boolean staff = hasRole(auth, "ADMIN") || hasRole(auth, "TEACHER");
        if (!staff && !auth.getName().equals(ownerUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own record.");
        }
    }
}
