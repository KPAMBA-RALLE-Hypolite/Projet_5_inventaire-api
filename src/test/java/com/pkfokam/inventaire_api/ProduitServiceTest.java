package com.pkfokam.inventaire_api;

import com.pkfokam.inventaire_api.dto.ProduitRequest;
import com.pkfokam.inventaire_api.dto.ProduitResponse;
import com.pkfokam.inventaire_api.dto.StockAlerteResponse;
import com.pkfokam.inventaire_api.entity.Produit;
import com.pkfokam.inventaire_api.exception.ResourceNotFoundException;
import com.pkfokam.inventaire_api.repository.ProduitRepository;
import com.pkfokam.inventaire_api.service.ProduitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — ProduitService")
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitServiceImpl produitService;

    private Produit produitNormal;
    private Produit produitStockFaible;
    private Produit produitRupture;

    @BeforeEach
    void setUp() {
        // Injecter le seuil d'alerte via ReflectionTestUtils (field injecté par @Value)
        ReflectionTestUtils.setField(produitService, "seuilAlerte", 5);

        LocalDateTime now = LocalDateTime.now();

        produitNormal = Produit.builder()
                .id(1L).nom("Ordinateur Dell").prix(1299.99).quantiteStock(25)
                .dateCreation(now).dateModification(now).build();

        produitStockFaible = Produit.builder()
                .id(2L).nom("Souris sans fil").prix(49.99).quantiteStock(3)
                .dateCreation(now).dateModification(now).build();

        produitRupture = Produit.builder()
                .id(3L).nom("Hub USB-C").prix(45.00).quantiteStock(0)
                .dateCreation(now).dateModification(now).build();
    }

    // ==========================================
    @Nested
    @DisplayName("Création de produit")
    class CreerProduit {

        @Test
        @DisplayName("Doit créer un produit avec stock normal")
        void creerProduit_StockNormal_Success() {
            ProduitRequest request = ProduitRequest.builder()
                    .nom("Nouveau produit").prix(99.99).quantiteStock(20).build();
            given(produitRepository.save(any(Produit.class))).willReturn(produitNormal);

            ProduitResponse result = produitService.creerProduit(request);

            assertThat(result).isNotNull();
            assertThat(result.getStatutStock()).isEqualTo("NORMAL");
            assertThat(result.isStockFaible()).isFalse();
            then(produitRepository).should(times(1)).save(any(Produit.class));
        }

        @Test
        @DisplayName("Doit créer un produit avec stock faible et logguer un avertissement")
        void creerProduit_StockFaible_LogsWarning() {
            ProduitRequest request = ProduitRequest.builder()
                    .nom("Produit critique").prix(29.99).quantiteStock(2).build();
            given(produitRepository.save(any(Produit.class))).willReturn(produitStockFaible);

            ProduitResponse result = produitService.creerProduit(request);

            assertThat(result.isStockFaible()).isTrue();
            assertThat(result.getStatutStock()).isEqualTo("FAIBLE");
        }
    }

    // ==========================================
    @Nested
    @DisplayName("Consultation de produits")
    class ObtenirProduits {

        @Test
        @DisplayName("Doit retourner tous les produits")
        void obtenirTousLesProduits_Success() {
            given(produitRepository.findAll())
                    .willReturn(List.of(produitNormal, produitStockFaible, produitRupture));

            List<ProduitResponse> result = produitService.obtenirTousLesProduits();

            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("Doit retourner un produit existant")
        void obtenirProduitParId_Trouve() {
            given(produitRepository.findById(1L)).willReturn(Optional.of(produitNormal));

            ProduitResponse result = produitService.obtenirProduitParId(1L);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNom()).isEqualTo("Ordinateur Dell");
        }

        @Test
        @DisplayName("Doit lever ResourceNotFoundException pour ID inconnu")
        void obtenirProduitParId_NonTrouve() {
            given(produitRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> produitService.obtenirProduitParId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ==========================================
    @Nested
    @DisplayName("Mise à jour de produit")
    class MettreAJour {

        @Test
        @DisplayName("Doit mettre à jour un produit existant")
        void mettreAJourProduit_Success() {
            ProduitRequest request = ProduitRequest.builder()
                    .nom("Dell XPS 15 Pro").prix(1499.99).quantiteStock(10).build();
            given(produitRepository.findById(1L)).willReturn(Optional.of(produitNormal));
            given(produitRepository.save(any(Produit.class))).willReturn(produitNormal);

            ProduitResponse result = produitService.mettreAJourProduit(1L, request);

            assertThat(result).isNotNull();
            then(produitRepository).should().save(any(Produit.class));
        }
    }

    // ==========================================
    @Nested
    @DisplayName("Suppression de produit")
    class SupprimerProduit {

        @Test
        @DisplayName("Doit supprimer un produit existant")
        void supprimerProduit_Success() {
            given(produitRepository.findById(1L)).willReturn(Optional.of(produitNormal));
            willDoNothing().given(produitRepository).delete(produitNormal);

            assertThatCode(() -> produitService.supprimerProduit(1L)).doesNotThrowAnyException();
            then(produitRepository).should().delete(produitNormal);
        }

        @Test
        @DisplayName("Doit lever une exception pour ID introuvable")
        void supprimerProduit_NonTrouve() {
            given(produitRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> produitService.supprimerProduit(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ==========================================
    @Nested
    @DisplayName("Alertes de stock")
    class AlertesStock {

        @Test
        @DisplayName("Doit retourner les produits en alerte")
        void obtenirAlerteStockFaible_AvecAlertes() {
            given(produitRepository.findByQuantiteStockLessThanOrderByQuantiteStockAsc(5))
                    .willReturn(List.of(produitRupture, produitStockFaible));

            StockAlerteResponse result = produitService.obtenirAlerteStockFaible();

            assertThat(result.getNombreProduitsCritiques()).isEqualTo(2);
            assertThat(result.getNombreProduitsEnRupture()).isEqualTo(1);
            assertThat(result.getSeuilAlerte()).isEqualTo(5);
            assertThat(result.getMessage()).contains("⚠️");
            assertThat(result.getProduitsEnAlerte()).hasSize(2);
        }

        @Test
        @DisplayName("Doit retourner un message OK quand aucune alerte")
        void obtenirAlerteStockFaible_SansAlerte() {
            given(produitRepository.findByQuantiteStockLessThanOrderByQuantiteStockAsc(5))
                    .willReturn(List.of());

            StockAlerteResponse result = produitService.obtenirAlerteStockFaible();

            assertThat(result.getNombreProduitsCritiques()).isZero();
            assertThat(result.getMessage()).contains("✅");
        }

        @Test
        @DisplayName("Statut RUPTURE si quantité = 0")
        void statut_Rupture_QuandQuantiteZero() {
            given(produitRepository.findById(3L)).willReturn(Optional.of(produitRupture));

            ProduitResponse result = produitService.obtenirProduitParId(3L);

            assertThat(result.getStatutStock()).isEqualTo("RUPTURE");
            assertThat(result.isStockFaible()).isTrue();
        }

        @Test
        @DisplayName("Statut FAIBLE si 0 < quantité < seuil")
        void statut_Faible_QuandEntrZeroEtSeuil() {
            given(produitRepository.findById(2L)).willReturn(Optional.of(produitStockFaible));

            ProduitResponse result = produitService.obtenirProduitParId(2L);

            assertThat(result.getStatutStock()).isEqualTo("FAIBLE");
            assertThat(result.isStockFaible()).isTrue();
        }

        @Test
        @DisplayName("Statut NORMAL si quantité >= seuil")
        void statut_Normal_QuandAuDessusSeuil() {
            given(produitRepository.findById(1L)).willReturn(Optional.of(produitNormal));

            ProduitResponse result = produitService.obtenirProduitParId(1L);

            assertThat(result.getStatutStock()).isEqualTo("NORMAL");
            assertThat(result.isStockFaible()).isFalse();
        }
    }
}
