package ism.mae.service.impl;

import java.util.List;
import java.util.Optional;
import ism.mae.entity.Produit;
import ism.mae.repository.ProduitRepository;
import ism.mae.service.ProduitService;

public class ProduitServiceImpl implements ProduitService {

    // Dépendance (Couplage faible avec l'interface)
    private ProduitRepository produitRepository;

    // Injection de dépendance par constructeur
    public ProduitServiceImpl(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    @Override
    public Produit addProduit(Produit produit) {
        // Logique métier (ex: validation) pourrait aller ici
        return produitRepository.insertProduit(produit);
    }

    @Override
    public List<Produit> getAllProduits() {
        return produitRepository.findAllProduits();
    }

    @Override
    public List<Produit> getProduitsByCategorie(int categorieId) {
        return produitRepository.findProduitsByCategorieId(categorieId);
    }

    @Override
    public Optional<Produit> getProduitById(int id) {
        return produitRepository.findById(id);
    }
}