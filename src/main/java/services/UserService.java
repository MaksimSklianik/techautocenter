package services;

import models.User;
import util.DatabaseUtil;
import util.PasswordUtil;
import util.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private static final String FIND_BY_ID_SQL = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_USERNAME_SQL = "SELECT * FROM users WHERE username = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM users ORDER BY full_name";
    private static final String SEARCH_SQL = "SELECT * FROM users WHERE username LIKE ? OR full_name LIKE ? ORDER BY full_name";
    private static final String INSERT_SQL =
            "INSERT INTO users (username, password, full_name, phone, role) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE users SET username = ?, full_name = ?, phone = ?, role = ?, active = ? WHERE id = ?";
    private static final String UPDATE_PASSWORD_SQL =
            "UPDATE users SET password = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM users WHERE id = ?";
    private static final String CHECK_USERNAME_EXISTS_SQL =
            "SELECT COUNT(*) FROM users WHERE username = ? AND id != ?";

    public Optional<User> findById(int id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при поиске пользователя по ID: " + id, e);
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_USERNAME_SQL)) {

            stmt.setString(1, username.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при поиске пользователя по имени: " + username, e);
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(FIND_ALL_SQL)) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при получении списка пользователей", e);
        }
        return users;
    }

    public List<User> search(String query) {
        List<User> users = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }

        String searchTerm = "%" + query.trim() + "%";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_SQL)) {

            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при поиске пользователей по запросу: " + query, e);
        }
        return users;
    }

    public boolean create(User user) {
        if (user == null || !validateUser(user)) {
            return false;
        }

        if (isUsernameExists(user.getUsername(), 0)) {
            logger.log(Level.WARNING, "Попытка создать пользователя с существующим именем: " + user.getUsername());
            return false;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getRole().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }

            logger.log(Level.INFO, "Создан новый пользователь: {0}", user.getUsername());
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при создании пользователя: " + user.getUsername(), e);
            return false;
        }
    }

    public boolean update(User user) {
        if (user == null || user.getId() <= 0 || !validateUser(user)) {
            return false;
        }

        if (isUsernameExists(user.getUsername(), user.getId())) {
            logger.log(Level.WARNING, "Попытка изменить имя пользователя на существующее: " + user.getUsername());
            return false;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getRole().name());
            stmt.setBoolean(5, user.isActive());
            stmt.setInt(6, user.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.log(Level.INFO, "Обновлены данные пользователя ID: {0}", user.getId());
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при обновлении пользователя ID: " + user.getId(), e);
        }
        return false;
    }

    public boolean updatePassword(int userId, String newPassword) {
        if (userId <= 0 || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_PASSWORD_SQL)) {

            stmt.setString(1, PasswordUtil.hashPassword(newPassword));
            stmt.setInt(2, userId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.log(Level.INFO, "Обновлен пароль пользователя ID: {0}", userId);
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при обновлении пароля пользователя ID: " + userId, e);
        }
        return false;
    }

    public boolean delete(int id) {
        if (id <= 0) {
            return false;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.log(Level.INFO, "Удален пользователь ID: {0}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при удалении пользователя ID: " + id, e);
        }
        return false;
    }

    public boolean isUsernameExists(String username, int excludeId) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_USERNAME_EXISTS_SQL)) {

            stmt.setString(1, username.trim());
            stmt.setInt(2, excludeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при проверке имени пользователя: " + username, e);
        }
        return false;
    }

    private boolean validateUser(User user) {
        if (user == null) {
            return false;
        }

        if (!Validator.isValidUsername(user.getUsername())) {
            logger.log(Level.WARNING, "Невалидное имя пользователя: " + user.getUsername());
            return false;
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            logger.log(Level.WARNING, "Пустой пароль для пользователя: " + user.getUsername());
            return false;
        }

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            logger.log(Level.WARNING, "Пустое полное имя для пользователя: " + user.getUsername());
            return false;
        }

        if (user.getPhone() != null && !user.getPhone().trim().isEmpty() &&
                !Validator.isValidPhone(user.getPhone())) {
            logger.log(Level.WARNING, "Невалидный телефон для пользователя: " + user.getUsername());
            return false;
        }

        return true;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("active"));
        return user;
    }

    public boolean addUser(User user) {
        return create(user);
    }

    public List<User> getAllUsers() {
        return findAll();
    }

    public boolean updateUser(User user) {
        return update(user);
    }

    public List<User> searchUsers(String query) {
        return search(query);
    }
}