package com.pkfokam.inventaire_api.repository;

import com.pkfokam.inventaire_api.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour l'entité {@link Produit}.
 */
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    /**
     * Recherche les produits dont le stock est inférieur au seuil donné.
     *
     * @param seuil le seuil de stock (ex. 5)
     * @return la liste des produits en stock faible, triés par quantité croissante
     */
    List<Produit> findByQuantiteStockLessThanOrderByQuantiteStockAsc(int seuil);

    /**
     * Recherche les produits dont le stock est exactement à zéro.
     *
     * @return la liste des produits épuisés
     */
    List<Produit> findByQuantiteStockEquals(int quantite);

    /**
     * Recherche par nom (insensible à la casse, partielle).
     *
     * @param nom la chaîne recherchée dans le nom
     * @return la liste des produits correspondants
     */
    List<Produit> findByNomContainingIgnoreCaseOrderByNomAsc(String nom);

    /**
     * Vérifie si un produit avec ce nom existe déjà (insensible à la casse).
     *
     * @param nom  le nom à vérifier
     * @param id   l'id à exclure (pour la mise à jour)
     * @return vrai si un doublon existe
     */
    @Query("SELECT COUNT(p) > 0 FROM Produit p WHERE LOWER(p.nom) = LOWER(:nom) AND p.id <> :id")
    boolean existsByNomIgnoreCaseAndIdNot(@Param("nom") String nom, @Param("id") Long id);

    /**
     * Récupère les produits triés par quantité de stock croissante.
     *
     * @return tous les produits triés par stock
     */
    List<Produit> findAllByOrderByQuantiteStockAsc();
}
