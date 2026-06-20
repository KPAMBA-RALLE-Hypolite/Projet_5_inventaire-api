package com.pkfokam.inventaire_api.service;


import com.pkfokam.inventaire_api.dto.ProduitRequest;
import com.pkfokam.inventaire_api.dto.ProduitResponse;
import com.pkfokam.inventaire_api.dto.StockAlerteResponse;

import java.util.List;

/**
 * Interface du service métier pour la gestion de l'inventaire de produits.
 */
public interface ProduitService {

    /**
     * Crée un nouveau produit dans l'inventaire.
     *
     * @param request les données du produit
     * @return le produit créé
     */
    ProduitResponse creerProduit(ProduitRequest request);

    List<ProduitResponse> obtenirTousLesProduits();

    ProduitResponse obtenirProduitParId(Long id);

    ProduitResponse mettreAJourProduit(Long id, ProduitRequest request);

    void supprimerProduit(Long id);


    StockAlerteResponse obtenirAlerteStockFaible();


    List<ProduitResponse> rechercherParNom(String nom);
}
