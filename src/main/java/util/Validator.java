package util;

import models.User;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+7|8]\\d{10}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidRole(String role) {
        try {
            User.Role.valueOf(role);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}