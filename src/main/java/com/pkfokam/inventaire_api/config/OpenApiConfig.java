package com.pkfokam.inventaire_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration de Swagger / OpenAPI 3.
 *
 * <p>Swagger UI : <a href="http://localhost:8080/swagger-ui.html">http://localhost:8080/swagger-ui.html</a></p>
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${inventaire.stock.seuil-alerte:5}")
    private int seuilAlerte;

    @Bean
    public OpenAPI inventaireOpenAPI() {
        Server devServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Serveur de développement");

        Contact contact = new Contact()
                .name("Équipe Inventaire API")
                .email("contact@inventaire-api.com")
                .url("https://github.com/votre-username/inventaire-api");

        Info info = new Info()
                .title("Inventaire API - Gestion de Produits & Stocks")
                .version("1.0.0")
                .description(String.format("""
                        ## API REST de gestion d'inventaire de produits
                        
                        Cette API permet de gérer un inventaire de produits avec suivi des stocks.
                        
                        ### Fonctionnalités
                        - **CRUD Produits** : Créer, lire, mettre à jour et supprimer des produits
                        - **Alertes de stock** : Détection automatique des produits en stock faible (< %d unités)
                        - **Recherche** : Recherche de produits par nom
                        
                        ### Statuts de stock
                        | Statut | Condition |
                        |--------|-----------|
                        | `NORMAL` | Quantité ≥ %d |
                        | `FAIBLE` | 0 < Quantité < %d |
                        | `RUPTURE` | Quantité = 0 |
                        
                        ### Console H2 (développement)
                        Accès : [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
                        - JDBC URL : `jdbc:h2:mem:inventairedb`
                        - Username : `sa` | Password : *(vide)*
                        """, seuilAlerte, seuilAlerte, seuilAlerte))
                .contact(contact)
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"));

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
