package com.e_commerce.inventory_service.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sku_code", nullable = false, unique = true)
    private String skuCode; // Unique code for product identification

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // Quantity available in stock

    @Column(name = "price", nullable = false)
    private Double price; // Price of the product

    @Column(name = "last_updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated; // Timestamp for the last update

    @PreUpdate
    public void updateTimestamp() {
        this.lastUpdated = new Date();
    }
}
