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
    private static final String INSERT_RECORD_SQL =
            "INSERT INTO service_records (date, client_name, client_phone, " +
                    "service_type, car_model, license_plate, cost, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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

    // Методы для работы с записями сервиса
    public boolean addServiceRecord(ServiceRecord record) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_RECORD_SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            setRecordParameters(stmt, record);

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

        if (stmt.getParameterMetaData().getParameterCount() > 7) {
            stmt.setString(8, record.getNotes());
        }

        if (stmt.getParameterMetaData().getParameterCount() > 10) {
            stmt.setString(9, record.getStatus());
            stmt.setInt(10, record.getAssignedMechanicId());
        }
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

    // Аналогичные методы для работы с запчастями и расписанием работ
    public boolean addSparePart(SparePart part) {
        // Реализация добавления запчасти
        return false;
    }

    public boolean updateSparePart(SparePart part) {
        // Реализация обновления запчасти
        return false;
    }

    public List<SparePart> getAllSpareParts() {
        // Реализация получения всех запчастей
        return new ArrayList<>();
    }

    public boolean scheduleWork(WorkSchedule schedule) {
        // Реализация планирования работы
        return false;
    }

    public List<WorkSchedule> getWorkScheduleForMechanic(int mechanicId, LocalDate date) {
        // Реализация получения расписания механика
        return new ArrayList<>();
    }

    public void savePayment(Payment payment) {
    }

    public List<WorkSchedule> getAllWorkSchedules() {
        return new ArrayList<>();
    }
}