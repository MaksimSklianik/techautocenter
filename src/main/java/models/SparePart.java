package models;

import java.time.LocalDateTime;
import java.util.Objects;

public class SparePart {
    private int id;
    private String name;
    private String code;
    private String description;  // Добавлено отсутствующее поле
    private String compatibleModels;
    private int quantity;
    private double price;
    private String supplier;
    private int minQuantity;
    private LocalDateTime createdAt;

    public SparePart() {
        this.minQuantity = 5;  // Установка значения по умолчанию в конструкторе
        this.createdAt = LocalDateTime.now();  // Автоматическая установка времени создания
    }

    public SparePart(String name, String code, int quantity, double price) {
        this();  // Вызов конструктора по умолчанию
        this.name = Objects.requireNonNull(name, "Название не может быть null");
        this.code = Objects.requireNonNull(code, "Код не может быть null");
        this.quantity = quantity;
        this.price = price;
    }

    // Геттеры и сеттеры с валидацией
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID не может быть отрицательным");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Название не может быть null");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = Objects.requireNonNull(code, "Код не может быть null");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Описание не может превышать 500 символов");
        }
        this.description = description;
    }

    public String getCompatibleModels() {
        return compatibleModels;
    }

    public void setCompatibleModels(String compatibleModels) {
        this.compatibleModels = compatibleModels;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Количество не может быть отрицательным");
        }
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }
        this.price = price;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        if (minQuantity < 0) {
            throw new IllegalArgumentException("Минимальное количество не может быть отрицательным");
        }
        this.minQuantity = minQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = Objects.requireNonNull(createdAt, "Дата создания не может быть null");
    }

    // Бизнес-метод для проверки низкого запаса
    public boolean isLowStock() {
        return quantity < minQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SparePart sparePart = (SparePart) o;
        return id == sparePart.id &&
                quantity == sparePart.quantity &&
                Double.compare(sparePart.price, price) == 0 &&
                minQuantity == sparePart.minQuantity &&
                Objects.equals(name, sparePart.name) &&
                Objects.equals(code, sparePart.code) &&
                Objects.equals(description, sparePart.description) &&
                Objects.equals(compatibleModels, sparePart.compatibleModels) &&
                Objects.equals(supplier, sparePart.supplier) &&
                Objects.equals(createdAt, sparePart.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code, description, compatibleModels, quantity, price, supplier, minQuantity, createdAt);
    }

    @Override
    public String toString() {
        return "SparePart{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", minQuantity=" + minQuantity +
                ", lowStock=" + isLowStock() +
                '}';
    }
}