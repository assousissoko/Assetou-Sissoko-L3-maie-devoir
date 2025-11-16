package ism.mae.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString(exclude = {"commande"}) // Exclure la commande du ToString
public class LigneCommande {
    private int id;
    private double quantite;
    private double prixUnitaire; // Le prix du produit au moment de la vente

    // Relation (Une Ligne appartient à Une Commande)
    private Commande commande;
    
    // Relation (Une Ligne concerne Un Produit)
    private Produit produit;
}