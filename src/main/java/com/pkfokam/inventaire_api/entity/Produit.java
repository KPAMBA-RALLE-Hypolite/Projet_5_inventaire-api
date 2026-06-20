package com.pkfokam.inventaire_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité représentant un produit dans l'inventaire.
 */
@Entity
@Table(name = "produits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nom;

    @Column(nullable = false)
    private Double prix;

    @Column(name = "quantite_stock", nullable = false)
    private Integer quantiteStock;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    /**
     * Seuil en-dessous duquel le stock est considéré comme faible.
     */
    public static final int SEUIL_STOCK_FAIBLE = 5;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.dateCreation = now;
        this.dateModification = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    /**
     * Retourne vrai si le stock est inférieur au seuil d'alerte.
     */
    public boolean estEnStockFaible() {
        return this.quantiteStock < SEUIL_STOCK_FAIBLE;
    }
}
