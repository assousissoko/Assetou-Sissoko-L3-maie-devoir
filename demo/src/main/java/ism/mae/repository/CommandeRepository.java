package ism.mae.repository;

import java.util.List;
import java.util.Optional;
import ism.mae.entity.Commande;

public interface CommandeRepository {
    
    /**
     * Insère une commande et ses lignes de manière transactionnelle.
     * Met également à jour le stock de produits.
     * @param commande L'objet Commande complet avec ses lignes.
     * @return La Commande sauvegardée (avec son ID BDD).
     */
    Commande insertCommande(Commande commande);
    
    // Liste toutes les commandes (sans les détails)
    List<Commande> findAllCommandes();
    
    // Récupère une commande par ID avec tous ses détails (lignes et produits)
    Optional<Commande> findById(int commandeId);
}