package ism.mae.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString(exclude = {"categorie"}) // Exclut la catégorie pour éviter les boucles d'affichage
public class Produit {
      private int id;
      private String name;
      private double qteStock;
      private double pu; // Prix Unitaire
      private Categorie categorie; // Relation: Un produit a une catégorie
}