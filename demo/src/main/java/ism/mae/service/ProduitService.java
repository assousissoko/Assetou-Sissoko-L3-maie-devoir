package ism.mae.service;

import java.util.List;
import java.util.Optional;
import ism.mae.entity.Produit;

public interface ProduitService {
    Produit addProduit(Produit produit);
    List<Produit> getAllProduits();
    List<Produit> getProduitsByCategorie(int categorieId);
    Optional<Produit> getProduitById(int id);
}