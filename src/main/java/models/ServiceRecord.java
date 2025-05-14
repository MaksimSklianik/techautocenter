package models;

import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.util.Objects;

public class ServiceRecord {
    private int id;
    private LocalDate date;
    private String clientName;
    private String clientPhone;
    private String serviceType;
    private String carModel;
    private String licensePlate;
    private double cost;
    private String status;
    private int assignedMechanicId;
    private String notes;

    public ServiceRecord() {
    }

    public ServiceRecord(LocalDate date, String clientName, String clientPhone,
                         String serviceType, String carModel, double cost) {
        this.date = date;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.serviceType = serviceType;
        this.carModel = carModel;
        this.cost = cost;
        this.status = "Новая";
    }

    // Геттеры и сеттеры
    public int idProperty() {
        return id;
    }

    public LocalDate dateProperty() {
        return date;
    }

    public ObservableValue<String> clientNameProperty() {
        return clientName;
    }

    public String serviceTypeProperty() {
        return serviceType;
    }

    public String carModelProperty() {
        return carModel;
    }

    public double costProperty() {
        return cost;
    }

    public String statusProperty() {
        return status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAssignedMechanicId() {
        return assignedMechanicId;
    }

    public void setAssignedMechanicId(int assignedMechanicId) {
        this.assignedMechanicId = assignedMechanicId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceRecord that = (ServiceRecord) o;
        return id == that.id &&
                Double.compare(that.cost, cost) == 0 &&
                assignedMechanicId == that.assignedMechanicId &&
                Objects.equals(date, that.date) &&
                Objects.equals(clientName, that.clientName) &&
                Objects.equals(clientPhone, that.clientPhone) &&
                Objects.equals(serviceType, that.serviceType) &&
                Objects.equals(carModel, that.carModel) &&
                Objects.equals(licensePlate, that.licensePlate) &&
                Objects.equals(status, that.status) &&
                Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, clientName, clientPhone, serviceType,
                carModel, licensePlate, cost, status, assignedMechanicId, notes);
    }

    @Override
    public String toString() {
        return "ServiceRecord{" +
                "id=" + id +
                ", date=" + date +
                ", clientName='" + clientName + '\'' +
                ", clientPhone='" + clientPhone + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", carModel='" + carModel + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", cost=" + cost +
                ", status='" + status + '\'' +
                ", assignedMechanicId=" + assignedMechanicId +
                ", notes='" + notes + '\'' +
                '}';
    }

    public ObservableValue<String> clientPhoneProperty() {
        return null;
    }
}