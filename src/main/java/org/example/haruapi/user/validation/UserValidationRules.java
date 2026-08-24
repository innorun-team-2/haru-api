package org.example.haruapi.user.validation;

public final class UserValidationRules {

    public static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\p{Punct})"
                    + "[A-Za-z\\d\\p{Punct}]+$";

    public static final String NICKNAME_PATTERN =
            "^(?=.*[A-Za-z])(?=.*\\p{Punct})[A-Za-z\\p{Punct}]+$";

    private UserValidationRules() {
    }
}
