package services;

import models.Payment;
import models.ServiceRecord;
import models.SparePart;
import models.WorkSchedule;
import util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataService {
    // SQL запросы для ServiceRecord
    private static final String INSERT_RECORD_SQL =
            "INSERT INTO service_records (date, client_name, client_phone, " +
                    "service_type, car_model, license_plate, cost, status, " +
                    "assigned_mechanic_id, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_RECORD_SQL =
            "UPDATE service_records SET date = ?, client_name = ?, client_phone = ?, " +
                    "service_type = ?, car_model = ?, license_plate = ?, cost = ?, " +
                    "status = ?, assigned_mechanic_id = ?, notes = ? WHERE id = ?";

    private static final String DELETE_RECORD_SQL =
            "DELETE FROM service_records WHERE id = ?";

    private static final String GET_ALL_RECORDS_SQL =
            "SELECT * FROM service_records ORDER BY date DESC";

    private static final String GET_RECORD_BY_ID_SQL =
            "SELECT * FROM service_records WHERE id = ?";

    private static final String GET_RECORDS_BY_STATUS_SQL =
            "SELECT * FROM service_records WHERE status = ? ORDER BY date DESC";

    private static final String GET_RECORDS_BY_DATE_RANGE_SQL =
            "SELECT * FROM service_records WHERE date BETWEEN ? AND ? ORDER BY date DESC";

    // SQL запросы для SparePart
    private static final String INSERT_SPARE_PART_SQL =
            "INSERT INTO spare_parts (name, description, quantity, price, " +
                    "compatible_models, supplier) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SPARE_PART_SQL =
            "UPDATE spare_parts SET name = ?, description = ?, quantity = ?, " +
                    "price = ?, compatible_models = ?, supplier = ? WHERE id = ?";

    private static final String GET_ALL_SPARE_PARTS_SQL =
            "SELECT * FROM spare_parts ORDER BY name";

    // SQL запросы для WorkSchedule
    private static final String INSERT_WORK_SCHEDULE_SQL =
            "INSERT INTO work_schedules (mechanic_id, record_id, start_time, " +
                    "end_time, status) VALUES (?, ?, ?, ?, ?)";

    private static final String GET_WORK_SCHEDULES_FOR_MECHANIC_SQL =
            "SELECT * FROM work_schedules WHERE mechanic_id = ? AND DATE(start_time) = ?";

    private static final String GET_ALL_WORK_SCHEDULES_SQL =
            "SELECT * FROM work_schedules ORDER BY start_time";

    // SQL запросы для Payment
    private static final String INSERT_PAYMENT_SQL =
            "INSERT INTO payments (record_id, amount, payment_date, payment_method, " +
                    "transaction_id, notes) VALUES (?, ?, ?, ?, ?, ?)";

    // Методы для работы с записями сервиса
    public boolean addServiceRecord(ServiceRecord record) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_RECORD_SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            setRecordParameters(stmt, record);
            stmt.setString(8, record.getStatus());
            stmt.setInt(9, record.getAssignedMechanicId());
            stmt.setString(10, record.getNotes());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    record.setId(generatedKeys.getInt(1));
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateServiceRecord(ServiceRecord record) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_RECORD_SQL)) {

            setRecordParameters(stmt, record);
            stmt.setString(8, record.getStatus());
            stmt.setInt(9, record.getAssignedMechanicId());
            stmt.setString(10, record.getNotes());
            stmt.setInt(11, record.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteServiceRecord(int id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_RECORD_SQL)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ServiceRecord getServiceRecordById(int id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_RECORD_BY_ID_SQL)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapServiceRecord(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ServiceRecord> getAllServiceRecords() {
        return getServiceRecords(GET_ALL_RECORDS_SQL);
    }

    public List<ServiceRecord> getServiceRecordsByStatus(String status) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_RECORDS_BY_STATUS_SQL)) {

            stmt.setString(1, status);
            return getServiceRecords(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ServiceRecord> getServiceRecordsByDateRange(LocalDate start, LocalDate end) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_RECORDS_BY_DATE_RANGE_SQL)) {

            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            return getServiceRecords(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Методы для работы с запчастями
    public boolean addSparePart(SparePart part) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SPARE_PART_SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, part.getName());
            stmt.setString(2, part.getDescription());
            stmt.setInt(3, part.getQuantity());
            stmt.setDouble(4, part.getPrice());
            stmt.setString(5, part.getCompatibleModels());
            stmt.setString(6, part.getSupplier());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    part.setId(generatedKeys.getInt(1));
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSparePart(SparePart part) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SPARE_PART_SQL)) {

            stmt.setString(1, part.getName());
            stmt.setString(2, part.getDescription());
            stmt.setInt(3, part.getQuantity());
            stmt.setDouble(4, part.getPrice());
            stmt.setString(5, part.getCompatibleModels());
            stmt.setString(6, part.getSupplier());
            stmt.setInt(7, part.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SparePart> getAllSpareParts() {
        List<SparePart> parts = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_SPARE_PARTS_SQL)) {

            while (rs.next()) {
                SparePart part = new SparePart();
                part.setId(rs.getInt("id"));
                part.setName(rs.getString("name"));
                part.setDescription(rs.getString("description"));
                part.setQuantity(rs.getInt("quantity"));
                part.setPrice(rs.getDouble("price"));
                part.setCompatibleModels(rs.getString("compatible_models"));
                part.setSupplier(rs.getString("supplier"));
                parts.add(part);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parts;
    }

    // Методы для работы с расписанием работ
    public boolean scheduleWork(WorkSchedule schedule) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_WORK_SCHEDULE_SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, schedule.getMechanicId());
            stmt.setInt(2, schedule.getRecordId());
            stmt.setTimestamp(3, Timestamp.valueOf(schedule.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(schedule.getEndTime()));
            stmt.setString(5, schedule.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    schedule.setId(generatedKeys.getInt(1));
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<WorkSchedule> getWorkScheduleForMechanic(int mechanicId, LocalDate date) {
        List<WorkSchedule> schedules = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_WORK_SCHEDULES_FOR_MECHANIC_SQL)) {

            stmt.setInt(1, mechanicId);
            stmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WorkSchedule schedule = new WorkSchedule();
                    schedule.setId(rs.getInt("id"));
                    schedule.setMechanic(rs.getInt("mechanic_id"));
                    schedule.setRecordId(rs.getInt("record_id"));
                    schedule.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                    schedule.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                    schedule.setStatus(rs.getString("status"));
                    schedules.add(schedule);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    public List<WorkSchedule> getAllWorkSchedules() {
        List<WorkSchedule> schedules = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_WORK_SCHEDULES_SQL)) {

            while (rs.next()) {
                WorkSchedule schedule = new WorkSchedule();
                    schedule.setId(rs.getInt("id"));
                    schedule.setMechanic(rs.getInt("mechanic_id"));
                    schedule.setRecordId(rs.getInt("record_id"));
                schedule.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                schedule.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                schedule.setStatus(rs.getString("status"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    // Методы для работы с платежами
    public void savePayment(Payment payment) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_PAYMENT_SQL)) {

            stmt.setInt(1, payment.getRecordId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setTimestamp(3, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setString(4, payment.getPaymentMethod());
            stmt.setString(5, payment.getTransactionId());
            stmt.setString(6, payment.getNotes());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Вспомогательные методы
    private List<ServiceRecord> getServiceRecords(String sql) {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return getServiceRecords(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<ServiceRecord> getServiceRecords(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            return getServiceRecords(rs);
        }
    }

    private List<ServiceRecord> getServiceRecords(ResultSet rs) throws SQLException {
        List<ServiceRecord> records = new ArrayList<>();
        while (rs.next()) {
            records.add(mapServiceRecord(rs));
        }
        return records;
    }

    private void setRecordParameters(PreparedStatement stmt, ServiceRecord record) throws SQLException {
        stmt.setDate(1, Date.valueOf(record.getDate()));
        stmt.setString(2, record.getClientName());
        stmt.setString(3, record.getClientPhone());
        stmt.setString(4, record.getServiceType());
        stmt.setString(5, record.getCarModel());
        stmt.setString(6, record.getLicensePlate());
        stmt.setDouble(7, record.getCost());
    }

    private ServiceRecord mapServiceRecord(ResultSet rs) throws SQLException {
        ServiceRecord record = new ServiceRecord();
        record.setId(rs.getInt("id"));
        record.setDate(rs.getDate("date").toLocalDate());
        record.setClientName(rs.getString("client_name"));
        record.setClientPhone(rs.getString("client_phone"));
        record.setServiceType(rs.getString("service_type"));
        record.setCarModel(rs.getString("car_model"));
        record.setLicensePlate(rs.getString("license_plate"));
        record.setCost(rs.getDouble("cost"));
        record.setStatus(rs.getString("status"));
        record.setAssignedMechanicId(rs.getInt("assigned_mechanic_id"));
        record.setNotes(rs.getString("notes"));
        return record;
    }
}