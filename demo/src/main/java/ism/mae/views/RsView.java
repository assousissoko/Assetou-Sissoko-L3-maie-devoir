package ism.mae.views;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import ism.mae.entity.Categorie;
import ism.mae.entity.Produit; // <-- 1. Importer Produit
import ism.mae.service.CategorieService;
import ism.mae.service.ProduitService; // <-- 2. Importer ProduitService

public  class RsView {

    private CategorieService categorieService;
    private ProduitService produitService; // <-- 3. Ajouter le service
    private  Scanner scanner=new Scanner(System.in);

    // 4. Mettre à jour le constructeur pour l'injection
    public RsView(CategorieService categorieService, ProduitService produitService) {
        this.categorieService = categorieService;
        this.produitService = produitService;
    }

    // ... (saisieCategorie et afficheCategories ne changent pas) ...
    public Categorie saisieCategorie(){
        String libelle;
        do {
            System.out.print("Entrer le Name de la categorie: ");
            libelle=scanner.nextLine();
        } while (libelle.isBlank());
          return Categorie.builder()
                          .name(libelle)
                          .build();
    }

   public  void afficheCategories(List<Categorie>categories){
       System.out.println("\n--- Liste des Catégories ---");
       for (Categorie categorie : categories) {
            System.out.println("ID: " + categorie.getId() + ", Nom: " + categorie.getName());
       }
   }

   // +++ NOUVELLES MÉTHODES POUR LES PRODUITS +++

   /**
    * Affiche la liste des produits (similaire à afficheCategories)
    */
   public void afficheProduits(List<Produit> produits) {
       System.out.println("\n--- Liste des Produits ---");
       for (Produit produit : produits) {
           System.out.println(
               "ID: " + produit.getId() + 
               ", Nom: " + produit.getName() +
               ", Qte Stock: " + produit.getQteStock() +
               ", Prix Unit: " + produit.getPu() +
               ", Catégorie: " + (produit.getCategorie() != null ? produit.getCategorie().getName() : "N/A")
           );
       }
   }

   /**
    * Saisie pour un nouveau produit
    */
   public Produit saisieProduit() {
        System.out.print("Entrer le Nom du produit: ");
        String nom = scanner.nextLine();
        
        System.out.print("Entrer la Quantité en Stock: ");
        double qteStock = scanner.nextDouble();
        
        System.out.print("Entrer le Prix Unitaire: ");
        double pu = scanner.nextDouble();
        scanner.nextLine(); // Consomme le \n

        // Sélection de la catégorie
        System.out.println("Sélectionnez une catégorie :");
        List<Categorie> categories = categorieService.getAllCategories();
        afficheCategories(categories);
        System.out.print("Entrez l'ID de la catégorie: ");
        int catId = scanner.nextInt();
        scanner.nextLine(); // Consomme le \n

        // Création de l'objet Categorie juste avec l'ID (suffisant pour l'insertion)
        Categorie categorieChoisie = Categorie.builder().id(catId).build();

        return Produit.builder()
                .name(nom)
                .qteStock(qteStock)
                .pu(pu)
                .categorie(categorieChoisie)
                .build();
   }


   // 5. Mettre à jour le menu
   public  int  menu(){
        System.out.println("\n=== Menu Responsable Stock ===");
        System.out.println("1- Ajouter Categorie");
        System.out.println("2- Lister Categories");
        System.out.println("3- Ajouter Produit"); // <-- Nouveau
        System.out.println("4- Lister Produits"); // <-- Nouveau
        System.out.println("5- Filtrer Produits par Categorie"); // <-- Nouveau
        System.out.println("6- Quitter"); // <-- Changé
        System.out.print("Faites votre choix: "); 
        
        int choix = -1;
        try {
            choix = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Erreur: Veuillez entrer un chiffre.");
        }
        
        scanner.nextLine();
        return choix;
   }

   // 6. Mettre à jour le main()
   public  void main(){
         int choix;
      do {
           choix= menu();
          switch (choix) {
             case 1:
                  System.out.println("\n--- Ajout d'une Catégorie ---");
                  Categorie categorie=saisieCategorie();
                  if (categorieService.getCategorieByName(categorie.getName()).isPresent()) {
                      System.out.println("ERREUR : Cette Categorie existe Deja");
                  }else{
                      categorieService.addCategorie(categorie);
                      System.out.println("SUCCES : Categorie ajoutée !");
                  }
                break;
             case 2:
                List<Categorie> categories=categorieService.getAllCategories();
                if (categories.isEmpty()) {
                    System.out.println("Il n'y a aucune catégorie pour le moment.");
                } else {
                    afficheCategories(categories);
                }
                break;
             
             // +++ NOUVEAUX CAS POUR LES PRODUITS +++
             case 3:
                System.out.println("\n--- Ajout d'un Produit ---");
                Produit produit = saisieProduit();
                produitService.addProduit(produit);
                System.out.println("SUCCES : Produit ajouté !");
                break;
             case 4:
                List<Produit> produits = produitService.getAllProduits();
                if (produits.isEmpty()) {
                    System.out.println("Il n'y a aucun produit pour le moment.");
                } else {
                    afficheProduits(produits);
                }
                break;
             case 5:
                System.out.println("\n--- Filtrer par Catégorie ---");
                System.out.println("Choisissez une catégorie :");
                List<Categorie> cats = categorieService.getAllCategories();
                afficheCategories(cats);
                System.out.print("Entrez l'ID de la catégorie: ");
                int catId = scanner.nextInt();
                scanner.nextLine();
                
                List<Produit> produitsFiltres = produitService.getProduitsByCategorie(catId);
                 if (produitsFiltres.isEmpty()) {
                    System.out.println("Il n'y a aucun produit dans cette catégorie.");
                } else {
                    afficheProduits(produitsFiltres);
                }
                break;

             case 6: // <-- Changé
                System.out.println("Déconnexion...");
                break;
             default:
                 if (choix != -1) {
                     System.out.println("Choix invalide, veuillez réessayer.");
                 }
                break;
          }
      } while (choix != 6); // <-- Changé
   }
}