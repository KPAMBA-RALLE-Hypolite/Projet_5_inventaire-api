package com.pkfokam.inventaire_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO pour la création et la mise à jour d'un produit.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Données pour créer ou mettre à jour un produit")
public class ProduitRequest {

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 2, max = 255, message = "Le nom doit contenir entre 2 et 255 caractères")
    @Schema(
            description = "Nom du produit",
            example = "Ordinateur portable Dell XPS 15",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nom;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Digits(integer = 8, fraction = 2, message = "Le prix doit avoir au maximum 2 décimales")
    @Schema(
            description = "Prix unitaire du produit (en euros)",
            example = "1299.99",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double prix;

    @NotNull(message = "La quantité en stock est obligatoire")
    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    @Schema(
            description = "Quantité disponible en stock",
            example = "25",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer quantiteStock;
}
