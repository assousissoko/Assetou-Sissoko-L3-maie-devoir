package ism.mae.entity;

import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString(exclude = {"produits"}) // Exclut les produits pour éviter les boucles d'affichage
public class Categorie {
       private int id;
       private String name;
      
       // Relation: Une catégorie a plusieurs produits
       @Setter(AccessLevel.NONE) // Le setter pour la liste est privé
       @Builder.Default // Indique à Lombok d'initialiser cette liste
       private ArrayList<Produit> produits = new ArrayList<>();
       
       // Méthode pour ajouter un produit à la liste
       public void addProduit(Produit produit) {
          this.produits.add(produit);
       }
}