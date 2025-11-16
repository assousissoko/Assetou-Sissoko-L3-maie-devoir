package ism.mae.service;

import java.util.List;
import java.util.Optional;
import ism.mae.entity.Commande;

public interface CommandeService {
    
    /**
     * Tente de créer une commande.
     * Vérifie le stock avant d'insérer.
     * @param commande La commande à créer.
     * @return true si la commande est créée, false si le stock est insuffisant.
     */
    boolean createCommande(Commande commande);
    
    List<Commande> listAllCommandes();
    Optional<Commande> getCommandeDetails(int commandeId);
}