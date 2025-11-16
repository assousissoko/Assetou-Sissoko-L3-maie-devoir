package ism.mae.service.impl;

import java.util.List;
import java.util.Optional;

import ism.mae.entity.Commande;
import ism.mae.entity.LigneCommande;
import ism.mae.entity.Produit;
import ism.mae.repository.CommandeRepository;
import ism.mae.repository.ProduitRepository;
import ism.mae.service.CommandeService;

public class CommandeServiceImpl implements CommandeService {

    // SOLID : Ce service dépend de deux repositories
    private CommandeRepository commandeRepository;
    private ProduitRepository produitRepository;

    // Injection des deux dépendances
    public CommandeServiceImpl(CommandeRepository commandeRepository, ProduitRepository produitRepository) {
        this.commandeRepository = commandeRepository;
        this.produitRepository = produitRepository;
    }

    @Override
    public boolean createCommande(Commande commande) {
        // === Logique Métier : Vérification du Stock ===
        for (LigneCommande ligne : commande.getLignes()) {
            // 1. Récupérer l'état actuel du produit en BDD
            Optional<Produit> produitOpt = produitRepository.findById(ligne.getProduit().getId());

            // 2. Vérifier si le produit existe et si le stock est suffisant
            if (produitOpt.isEmpty()) {
                System.err.println("Erreur: Produit ID " + ligne.getProduit().getId() + " n'existe pas.");
                return false; // Échec de la commande
            }

            Produit produitEnStock = produitOpt.get();
            if (produitEnStock.getQteStock() < ligne.getQuantite()) {
                System.err.println("Erreur: Stock insuffisant pour " + produitEnStock.getName() + 
                                   ". Demandé: " + ligne.getQuantite() + ", Stock: " + produitEnStock.getQteStock());
                return false; // Échec de la commande
            }
        }
        
        // Si toutes les vérifications sont OK, on passe à l'insertion
        // Le repository gère la transaction (insertion + mise à jour du stock)
        commandeRepository.insertCommande(commande);
        return true; // Succès
    }

    @Override
    public List<Commande> listAllCommandes() {
        return commandeRepository.findAllCommandes();
    }

    @Override
    public Optional<Commande> getCommandeDetails(int commandeId) {
        return commandeRepository.findById(commandeId);
    }
}