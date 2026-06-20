package com.pkfokam.inventaire_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Enveloppe générique standardisée pour toutes les réponses de l'API.
 *
 * @param <T> le type des données retournées
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Réponse standard de l'API")
public class ApiResponse<T> {

    @Schema(description = "Indique si la requête a réussi", example = "true")
    private boolean success;

    @Schema(description = "Message informatif", example = "Produit créé avec succès")
    private String message;

    @Schema(description = "Données retournées")
    private T data;

    @Schema(description = "Horodatage de la réponse")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
