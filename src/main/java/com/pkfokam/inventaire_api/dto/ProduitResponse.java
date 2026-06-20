package com.pkfokam.inventaire_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour un produit de l'inventaire.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Données d'un produit retournées par l'API")
public class ProduitResponse {

    @Schema(description = "Identifiant unique du produit", example = "1")
    private Long id;

    @Schema(description = "Nom du produit", example = "Ordinateur portable Dell XPS 15")
    private String nom;

    @Schema(description = "Prix unitaire en euros", example = "1299.99")
    private Double prix;

    @Schema(description = "Quantité disponible en stock", example = "25")
    private Integer quantiteStock;

    @Schema(description = "Indique si le stock est inférieur au seuil d'alerte (< 5 unités)", example = "false")
    private boolean stockFaible;

    @Schema(description = "Statut du stock", example = "NORMAL", allowableValues = {"NORMAL", "FAIBLE", "RUPTURE"})
    private String statutStock;

    @Schema(description = "Date de création du produit", example = "2024-06-15T10:00:00")
    private LocalDateTime dateCreation;

    @Schema(description = "Date de dernière modification", example = "2024-06-20T14:30:00")
    private LocalDateTime dateModification;
}
