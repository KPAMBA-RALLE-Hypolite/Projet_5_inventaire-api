package com.pkfokam.inventaire_api.service;

import com.pkfokam.inventaire_api.dto.*;
import com.pkfokam.inventaire_api.entity.Produit;
import com.pkfokam.inventaire_api.exception.ResourceNotFoundException;
import com.pkfokam.inventaire_api.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion de l'inventaire de produits.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;

    @Value("${inventaire.stock.seuil-alerte:5}")
    private int seuilAlerte;

    // ==========================================
    // CRUD
    // ==========================================

    @Override
    public ProduitResponse creerProduit(ProduitRequest request) {
        log.info("Création du produit : {}", request.getNom());

        Produit produit = Produit.builder()
                .nom(request.getNom().trim())
                .prix(request.getPrix())
                .quantiteStock(request.getQuantiteStock())
                .build();

        Produit saved = produitRepository.save(produit);
        log.info("Produit créé — ID : {}, Nom : {}", saved.getId(), saved.getNom());

        // Alerte si le stock initial est déjà faible
        if (saved.estEnStockFaible()) {
            log.warn("⚠️  Produit '{}' créé avec un stock faible : {} unité(s)", saved.getNom(), saved.getQuantiteStock());
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduitResponse> obtenirTousLesProduits() {
        log.debug("Récupération de tous les produits");
        return produitRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProduitResponse obtenirProduitParId(Long id) {
        log.debug("Récupération du produit ID : {}", id);
        Produit produit = findProduitOrThrow(id);
        return mapToResponse(produit);
    }

    @Override
    public ProduitResponse mettreAJourProduit(Long id, ProduitRequest request) {
        log.info("Mise à jour du produit ID : {}", id);
        Produit produit = findProduitOrThrow(id);

        int ancienneQuantite = produit.getQuantiteStock();

        produit.setNom(request.getNom().trim());
        produit.setPrix(request.getPrix());
        produit.setQuantiteStock(request.getQuantiteStock());

        Produit updated = produitRepository.save(produit);

        // Log si transition vers stock faible
        if (ancienneQuantite >= seuilAlerte && updated.getQuantiteStock() < seuilAlerte) {
            log.warn("⚠️  Produit '{}' passé en stock faible : {} → {} unité(s)",
                    updated.getNom(), ancienneQuantite, updated.getQuantiteStock());
        }

        log.info("Produit mis à jour — ID : {}", updated.getId());
        return mapToResponse(updated);
    }

    @Override
    public void supprimerProduit(Long id) {
        log.info("Suppression du produit ID : {}", id);
        Produit produit = findProduitOrThrow(id);
        produitRepository.delete(produit);
        log.info("Produit '{}' supprimé avec succès", produit.getNom());
    }

    // ==========================================
    // Gestion des stocks
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public StockAlerteResponse obtenirAlerteStockFaible() {
        log.info("Génération du rapport d'alertes stock (seuil : {})", seuilAlerte);

        List<Produit> produitsEnAlerte = produitRepository
                .findByQuantiteStockLessThanOrderByQuantiteStockAsc(seuilAlerte);

        long nbRupture = produitsEnAlerte.stream()
                .filter(p -> p.getQuantiteStock() == 0)
                .count();

        List<ProduitResponse> alerteDtos = produitsEnAlerte.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        String message = construireMessageAlerte(produitsEnAlerte.size(), (int) nbRupture);

        log.info("Rapport généré — {} produit(s) en alerte, {} en rupture",
                produitsEnAlerte.size(), nbRupture);

        return StockAlerteResponse.builder()
                .seuilAlerte(seuilAlerte)
                .nombreProduitsCritiques(produitsEnAlerte.size())
                .nombreProduitsEnRupture((int) nbRupture)
                .dateRapport(LocalDateTime.now())
                .produitsEnAlerte(alerteDtos)
                .message(message)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduitResponse> rechercherParNom(String nom) {
        log.debug("Recherche produits par nom : '{}'", nom);
        return produitRepository.findByNomContainingIgnoreCaseOrderByNomAsc(nom)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // Méthodes privées utilitaires
    // ==========================================

    private Produit findProduitOrThrow(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", id));
    }

    private ProduitResponse mapToResponse(Produit produit) {
        StatutStock statut = StatutStock.determiner(produit.getQuantiteStock(), seuilAlerte);
        return ProduitResponse.builder()
                .id(produit.getId())
                .nom(produit.getNom())
                .prix(produit.getPrix())
                .quantiteStock(produit.getQuantiteStock())
                .stockFaible(statut != StatutStock.NORMAL)
                .statutStock(statut.name())
                .dateCreation(produit.getDateCreation())
                .dateModification(produit.getDateModification())
                .build();
    }

    private String construireMessageAlerte(int nbAlerte, int nbRupture) {
        if (nbAlerte == 0) {
            return "✅ Tous les stocks sont au-dessus du seuil d'alerte. Aucune action requise.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("⚠️  %d produit(s) nécessitent un réapprovisionnement.", nbAlerte));
        if (nbRupture > 0) {
            sb.append(String.format(" 🚨 %d en rupture totale !", nbRupture));
        }
        return sb.toString();
    }
}
