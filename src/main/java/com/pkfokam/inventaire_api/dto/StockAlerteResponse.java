package com.pkfokam.inventaire_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de réponse pour le rapport d'alertes de stock.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Rapport d'alertes de stock faible")
public class StockAlerteResponse {

    @Schema(description = "Seuil d'alerte configuré", example = "5")
    private int seuilAlerte;

    @Schema(description = "Nombre total de produits en alerte", example = "3")
    private int nombreProduitsCritiques;

    @Schema(description = "Nombre de produits en rupture totale (stock = 0)", example = "1")
    private int nombreProduitsEnRupture;

    @Schema(description = "Date et heure de génération du rapport")
    private LocalDateTime dateRapport;

    @Schema(description = "Liste des produits avec un stock inférieur au seuil")
    private List<ProduitResponse> produitsEnAlerte;

    @Schema(description = "Message d'alerte global", example = "⚠️ 3 produit(s) nécessitent un réapprovisionnement urgent !")
    private String message;
}
