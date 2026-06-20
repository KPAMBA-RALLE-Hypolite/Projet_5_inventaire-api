package com.pkfokam.inventaire_api.controller;

import com.pkfokam.inventaire_api.dto.ApiResponse;
import com.pkfokam.inventaire_api.dto.ProduitRequest;
import com.pkfokam.inventaire_api.dto.ProduitResponse;
import com.pkfokam.inventaire_api.dto.StockAlerteResponse;
import com.pkfokam.inventaire_api.service.ProduitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Produits", description = "API de gestion de l'inventaire de produits")
public class ProduitController {

    private final ProduitService produitService;

    // ==========================================
    // POST /api/produits
    // ==========================================

    @PostMapping
    @Operation(
            summary = "Créer un produit",
            description = "Ajoute un nouveau produit à l'inventaire. Un avertissement est logué si le stock initial est inférieur au seuil d'alerte."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Produit créé avec succès",
                    content = @Content(schema = @Schema(implementation = ProduitResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ApiResponse<ProduitResponse>> creerProduit(
            @Valid @RequestBody ProduitRequest request) {
        log.info("POST /api/produits — {}", request.getNom());
        ProduitResponse produit = produitService.creerProduit(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Produit créé avec succès", produit));
    }

    // ==========================================
    // GET /api/produits
    // ==========================================

    @GetMapping
    @Operation(
            summary = "Lister tous les produits",
            description = "Retourne la liste complète des produits de l'inventaire avec leur statut de stock."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    public ResponseEntity<ApiResponse<List<ProduitResponse>>> obtenirTousLesProduits() {
        log.info("GET /api/produits");
        List<ProduitResponse> produits = produitService.obtenirTousLesProduits();
        return ResponseEntity.ok(
                ApiResponse.success(String.format("%d produit(s) dans l'inventaire", produits.size()), produits)
        );
    }

    // ==========================================
    // GET /api/produits/{id}
    // ==========================================

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtenir un produit par ID",
            description = "Retourne le détail complet d'un produit avec son statut de stock."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Produit trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public ResponseEntity<ApiResponse<ProduitResponse>> obtenirProduitParId(
            @Parameter(description = "Identifiant du produit", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/produits/{}", id);
        ProduitResponse produit = produitService.obtenirProduitParId(id);
        return ResponseEntity.ok(ApiResponse.success("Produit récupéré avec succès", produit));
    }

    // ==========================================
    // PUT /api/produits/{id}
    // ==========================================

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un produit",
            description = "Modifie le nom, le prix et/ou la quantité en stock d'un produit existant. La date de modification est automatiquement mise à jour."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Produit mis à jour"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public ResponseEntity<ApiResponse<ProduitResponse>> mettreAJourProduit(
            @Parameter(description = "Identifiant du produit", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProduitRequest request) {
        log.info("PUT /api/produits/{}", id);
        ProduitResponse produit = produitService.mettreAJourProduit(id, request);
        return ResponseEntity.ok(ApiResponse.success("Produit mis à jour avec succès", produit));
    }

    // ==========================================
    // DELETE /api/produits/{id}
    // ==========================================

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un produit",
            description = "Supprime définitivement un produit de l'inventaire."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Produit supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public ResponseEntity<ApiResponse<Void>> supprimerProduit(
            @Parameter(description = "Identifiant du produit", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/produits/{}", id);
        produitService.supprimerProduit(id);
        return ResponseEntity.ok(ApiResponse.success("Produit supprimé avec succès"));
    }

    // ==========================================
    // GET /api/produits/stock/alertes
    // ==========================================

    @GetMapping("/stock/alertes")
    @Operation(
            summary = "Rapport des alertes de stock faible",
            description = """
                    Génère un rapport complet des produits dont le stock est inférieur au seuil d'alerte (< 5 unités par défaut).
                    
                    **Statuts de stock :**
                    - `NORMAL` : stock ≥ seuil
                    - `FAIBLE` : 0 < stock < seuil
                    - `RUPTURE` : stock = 0
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rapport généré avec succès")
    public ResponseEntity<ApiResponse<StockAlerteResponse>> obtenirAlertes() {
        log.info("GET /api/produits/stock/alertes");
        StockAlerteResponse alerte = produitService.obtenirAlerteStockFaible();
        return ResponseEntity.ok(ApiResponse.success(alerte.getMessage(), alerte));
    }

    // ==========================================
    // GET /api/produits/recherche?nom=...
    // ==========================================

    @GetMapping("/recherche")
    @Operation(
            summary = "Rechercher des produits par nom",
            description = "Recherche partielle et insensible à la casse sur le nom du produit."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Résultats de recherche")
    public ResponseEntity<ApiResponse<List<ProduitResponse>>> rechercherParNom(
            @Parameter(description = "Terme de recherche", example = "laptop")
            @RequestParam String nom) {
        log.info("GET /api/produits/recherche?nom={}", nom);
        List<ProduitResponse> produits = produitService.rechercherParNom(nom);
        return ResponseEntity.ok(
                ApiResponse.success(String.format("%d produit(s) trouvé(s) pour '%s'", produits.size(), nom), produits)
        );
    }
}
