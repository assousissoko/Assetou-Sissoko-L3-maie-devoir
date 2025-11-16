package ism.mae.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString(exclude = {"lignes"}) // Exclure la liste du ToString pour éviter les boucles
public class Commande {
    private int id;
    private LocalDate dateCommande;
    private double montantTotal;

    // Relation (Une Commande a Plusieurs Lignes)
    @Builder.Default // Indique à Lombok de toujours initialiser cette liste
    private List<LigneCommande> lignes = new ArrayList<>();

    // Méthode utilitaire pour ajouter une ligne
    public void addLigne(LigneCommande ligne) {
        lignes.add(ligne);
        ligne.setCommande(this); // Lie la ligne à cette commande
    }
}