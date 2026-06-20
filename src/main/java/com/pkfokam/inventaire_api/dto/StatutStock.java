package com.pkfokam.inventaire_api.dto;

/**
 * Enumération des statuts possibles d'un stock.
 */
public enum StatutStock {

    /** Stock normal, au-dessus du seuil d'alerte. */
    NORMAL,

    /** Stock faible, inférieur au seuil d'alerte (< 5) mais supérieur à 0. */
    FAIBLE,

    /** Rupture de stock totale (quantité = 0). */
    RUPTURE;

    /**
     * Détermine le statut en fonction de la quantité et du seuil.
     *
     * @param quantite la quantité en stock
     * @param seuil    le seuil d'alerte configuré
     * @return le statut correspondant
     */
    public static StatutStock determiner(int quantite, int seuil) {
        if (quantite == 0) return RUPTURE;
        if (quantite < seuil) return FAIBLE;
        return NORMAL;
    }
}
