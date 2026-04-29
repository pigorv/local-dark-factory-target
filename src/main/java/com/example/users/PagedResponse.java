package com.example.users;

import java.util.List;

public record PagedResponse<T>(List<T> data, String nextCursor) {
}
