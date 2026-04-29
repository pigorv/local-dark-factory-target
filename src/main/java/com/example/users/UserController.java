package com.example.users;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Object list(
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) Integer offset,
        @RequestParam(defaultValue = "10") int limit
    ) {
        // Limit validation to prevent excessive data retrieval
        int safeLimit = Math.min(limit, 100);

        // Cursor-based pagination takes precedence
        if (cursor != null || offset == null) {
            Long cursorId = parseCursor(cursor);
            List<User> page = repository.findAfter(cursorId, safeLimit);
            String nextCursor = page.isEmpty() ? null : String.valueOf(page.get(page.size() - 1).id());
            return new PagedResponse<>(page, nextCursor);
        }

        // Legacy offset-based pagination for backward compatibility
        List<User> page = repository.findAll(offset, safeLimit);
        return Map.of(
            "items", page,
            "offset", offset,
            "limit", safeLimit,
            "total", repository.total()
        );
    }

    private Long parseCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(cursor);
        } catch (NumberFormatException e) {
            // Invalid cursor, start from beginning
            return null;
        }
    }
}
