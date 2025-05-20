package home.example.board.utils;

import home.example.board.DTO.CustomUserDetail;

public class CheckAdminUserUtils {
    public static void isAdminOrThrowException(CustomUserDetail userDetail) {
        if (userDetail == null) {
            throw new IllegalArgumentException("User detail cannot be null");
        }
        if (!userDetail.isAdmin()) {
            throw new IllegalArgumentException("User is not an admin");
        }
    }
}
