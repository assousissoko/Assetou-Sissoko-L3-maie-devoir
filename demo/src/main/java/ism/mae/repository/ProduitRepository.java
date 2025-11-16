package ism.mae.repository;

import java.util.List;
import java.util.Optional;
import ism.mae.entity.Produit;

public interface ProduitRepository {
    // Insère un nouveau produit dans la BDD
    Produit insertProduit(Produit produit);
    
    // Met à jour le stock d'un produit (essentiel pour les commandes)
    Produit updateStock(Produit produit); 
    
    // Liste tous les produits
    List<Produit> findAllProduits();
    
    // Filtre les produits par ID de catégorie
    List<Produit> findProduitsByCategorieId(int categorieId);
    
    // Trouve un produit par son ID (utile pour les commandes)
    Optional<Produit> findById(int id);
}