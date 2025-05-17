package controllers;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import models.ServiceRecord;

public class WorkSchedule {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty clientName = new SimpleStringProperty();
    private final StringProperty clientPhone = new SimpleStringProperty();
    private final StringProperty serviceType = new SimpleStringProperty();
    private final StringProperty carModel = new SimpleStringProperty();
    private final StringProperty licensePlate = new SimpleStringProperty();
    private final DoubleProperty cost = new SimpleDoubleProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final IntegerProperty assignedMechanicId = new SimpleIntegerProperty();
    private final StringProperty notes = new SimpleStringProperty();
    private ServiceRecord serviceRecord;

    // Конструкторы
    public WorkSchedule() {
        this.status.set("Запланировано");
    }

    public WorkSchedule(int id, LocalDate date, String clientName, String serviceType,
                        String carModel, double cost, String status) {
        this();
        setId(id);
        setDate(date);
        setClientName(clientName);
        setServiceType(serviceType);
        setCarModel(carModel);
        setCost(cost);
        setStatus(status);
    }

    // Property методы
    public IntegerProperty idProperty() { return id; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty clientNameProperty() { return clientName; }
    public StringProperty clientPhoneProperty() { return clientPhone; }
    public StringProperty serviceTypeProperty() { return serviceType; }
    public StringProperty carModelProperty() { return carModel; }
    public StringProperty licensePlateProperty() { return licensePlate; }
    public DoubleProperty costProperty() { return cost; }
    public StringProperty statusProperty() { return status; }
    public IntegerProperty assignedMechanicIdProperty() { return assignedMechanicId; }
    public StringProperty notesProperty() { return notes; }

    // Геттеры и сеттеры
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public LocalDate getDate() { return date.get(); }
    public void setDate(LocalDate date) { this.date.set(date); }

    public String getClientName() { return clientName.get(); }
    public void setClientName(String clientName) { this.clientName.set(clientName); }

    public String getClientPhone() { return clientPhone.get(); }
    public void setClientPhone(String clientPhone) { this.clientPhone.set(clientPhone); }

    public String getServiceType() { return serviceType.get(); }
    public void setServiceType(String serviceType) { this.serviceType.set(serviceType); }

    public String getCarModel() { return carModel.get(); }
    public void setCarModel(String carModel) { this.carModel.set(carModel); }

    public String getLicensePlate() { return licensePlate.get(); }
    public void setLicensePlate(String licensePlate) { this.licensePlate.set(licensePlate); }

    public double getCost() { return cost.get(); }
    public void setCost(double cost) { this.cost.set(cost); }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }

    public int getAssignedMechanicId() { return assignedMechanicId.get(); }
    public void setAssignedMechanicId(int assignedMechanicId) { this.assignedMechanicId.set(assignedMechanicId); }

    public String getNotes() { return notes.get(); }
    public void setNotes(String notes) { this.notes.set(notes); }

    public ServiceRecord getServiceRecord() { return serviceRecord; }
    public void setServiceRecord(ServiceRecord serviceRecord) { this.serviceRecord = serviceRecord; }

    // equals, hashCode и toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkSchedule that = (WorkSchedule) o;

        if (getId() != that.getId()) return false;
        if (Double.compare(that.getCost(), getCost()) != 0) return false;
        if (getAssignedMechanicId() != that.getAssignedMechanicId()) return false;
        if (getDate() != null ? !getDate().equals(that.getDate()) : that.getDate() != null) return false;
        if (getClientName() != null ? !getClientName().equals(that.getClientName()) : that.getClientName() != null)
            return false;
        if (getClientPhone() != null ? !getClientPhone().equals(that.getClientPhone()) : that.getClientPhone() != null)
            return false;
        if (getServiceType() != null ? !getServiceType().equals(that.getServiceType()) : that.getServiceType() != null)
            return false;
        if (getCarModel() != null ? !getCarModel().equals(that.getCarModel()) : that.getCarModel() != null)
            return false;
        if (getLicensePlate() != null ? !getLicensePlate().equals(that.getLicensePlate()) : that.getLicensePlate() != null)
            return false;
        if (getStatus() != null ? !getStatus().equals(that.getStatus()) : that.getStatus() != null) return false;
        return getNotes() != null ? getNotes().equals(that.getNotes()) : that.getNotes() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getId();
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        result = 31 * result + (getClientName() != null ? getClientName().hashCode() : 0);
        result = 31 * result + (getClientPhone() != null ? getClientPhone().hashCode() : 0);
        result = 31 * result + (getServiceType() != null ? getServiceType().hashCode() : 0);
        result = 31 * result + (getCarModel() != null ? getCarModel().hashCode() : 0);
        result = 31 * result + (getLicensePlate() != null ? getLicensePlate().hashCode() : 0);
        temp = Double.doubleToLongBits(getCost());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        result = 31 * result + getAssignedMechanicId();
        result = 31 * result + (getNotes() != null ? getNotes().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WorkSchedule{" +
                "id=" + getId() +
                ", date=" + getDate() +
                ", clientName='" + getClientName() + '\'' +
                ", clientPhone='" + getClientPhone() + '\'' +
                ", serviceType='" + getServiceType() + '\'' +
                ", carModel='" + getCarModel() + '\'' +
                ", licensePlate='" + getLicensePlate() + '\'' +
                ", cost=" + getCost() +
                ", status='" + getStatus() + '\'' +
                ", assignedMechanicId=" + getAssignedMechanicId() +
                ", notes='" + getNotes() + '\'' +
                ", serviceRecord=" + (serviceRecord != null ? serviceRecord.getId() : "null") +
                '}';
    }
}