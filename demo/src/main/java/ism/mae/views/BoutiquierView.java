package ism.mae.views;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import ism.mae.entity.Commande;
import ism.mae.entity.LigneCommande;
import ism.mae.entity.Produit;
import ism.mae.service.CommandeService;
import ism.mae.service.ProduitService;

public class BoutiquierView {

    // Dépendances (services)
    private CommandeService commandeService;
    private ProduitService produitService;
    private Scanner scanner = new Scanner(System.in);

    // Injection de dépendance par constructeur
    public BoutiquierView(CommandeService commandeService, ProduitService produitService) {
        this.commandeService = commandeService;
        this.produitService = produitService;
    }

    /**
     * Affiche le menu principal du Boutiquier.
     * @return int Le choix de l'utilisateur.
     */
    public int menu() {
        System.out.println("\n=== Menu Boutiquier ===");
        System.out.println("1- Enregistrer une Commande");
        System.out.println("2- Lister les Commandes");
        System.out.println("3- Voir les Détails d'une Commande");
        System.out.println("4- Quitter");
        System.out.print("Faites votre choix: ");

        int choix = -1;
        try {
            choix = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Erreur: Veuillez entrer un chiffre.");
        }
        
        scanner.nextLine(); // Consomme le \n
        return choix;
    }

    /**
     * Boucle principale de la vue Boutiquier.
     */
    public void main() {
        int choix;
        do {
            choix = menu();
            switch (choix) {
                case 1:
                    enregistrerCommande();
                    break;
                case 2:
                    listerCommandes();
                    break;
                case 3:
                    voirDetailsCommande();
                    break;
                case 4:
                    System.out.println("Déconnexion...");
                    break;
                default:
                    if (choix != -1) {
                        System.out.println("Choix invalide, veuillez réessayer.");
                    }
                    break;
            }
        } while (choix != 4);
    }

    /**
     * Logique pour enregistrer une nouvelle commande (Cas 1).
     */
    private void enregistrerCommande() {
        System.out.println("\n--- Enregistrement d'une Commande ---");
        
        // 1. Initialiser la commande et le "panier"
        Commande commande = Commande.builder()
                .dateCommande(LocalDate.now()) // Date du jour
                .lignes(new ArrayList<>())
                .build();
        
        double montantTotal = 0.0;
        String continuer;

        do {
            // 2. Afficher les produits disponibles
            System.out.println("Produits disponibles :");
            List<Produit> produits = produitService.getAllProduits();
            afficheProduits(produits); // Réutilise la méthode d'affichage

            // 3. Demander le produit et la quantité
            System.out.print("Entrez l'ID du produit à ajouter au panier: ");
            int produitId = scanner.nextInt();
            System.out.print("Entrez la quantité désirée: ");
            double quantite = scanner.nextDouble();
            scanner.nextLine(); // Consomme le \n

            // 4. Vérifier si le produit existe
            Optional<Produit> produitOpt = produitService.getProduitById(produitId);

            if (produitOpt.isPresent()) {
                Produit produit = produitOpt.get();
                
                // 5. Créer la ligne de commande
                LigneCommande ligne = LigneCommande.builder()
                        .produit(produit)
                        .quantite(quantite)
                        .prixUnitaire(produit.getPu()) // Utilise le prix actuel du produit
                        .build();
                
                // 6. Ajouter au panier et mettre à jour le montant
                commande.addLigne(ligne);
                montantTotal += (produit.getPu() * quantite);
                
                System.out.println("Produit '" + produit.getName() + "' ajouté au panier.");

            } else {
                System.out.println("ERREUR: Produit non trouvé.");
            }

            // 7. Continuer ?
            System.out.print("Voulez-vous ajouter un autre produit ? (o/n): ");
            continuer = scanner.nextLine();

        } while (continuer.equalsIgnoreCase("o"));

        // 8. Finaliser la commande
        if (commande.getLignes().isEmpty()) {
            System.out.println("Commande annulée (panier vide).");
            return;
        }
        
        commande.setMontantTotal(montantTotal);

        // 9. Envoyer au service (qui vérifie le stock et insère en BDD)
        boolean succes = commandeService.createCommande(commande);

        if (succes) {
            System.out.println("SUCCES : Commande enregistrée ! Montant total: " + montantTotal + " FCFA");
        } else {
            System.out.println("ERREUR : La commande n'a pas pu être enregistrée (stock insuffisant ou autre problème).");
        }
    }

    /**
     * Logique pour lister toutes les commandes (Cas 2).
     */
    private void listerCommandes() {
        System.out.println("\n--- Liste de toutes les Commandes ---");
        List<Commande> commandes = commandeService.listAllCommandes();

        if (commandes.isEmpty()) {
            System.out.println("Il n'y a aucune commande enregistrée.");
            return;
        }

        // Affichage simple
        for (Commande cmd : commandes) {
            System.out.println(
                "ID: " + cmd.getId() + 
                ", Date: " + cmd.getDateCommande() + 
                ", Montant Total: " + cmd.getMontantTotal() + " FCFA"
            );
        }
    }

    /**
     * Logique pour voir les détails d'une commande (Cas 3).
     */
    private void voirDetailsCommande() {
        System.out.println("\n--- Détails d'une Commande ---");
        System.out.print("Entrez l'ID de la commande: ");
        int commandeId = scanner.nextInt();
        scanner.nextLine(); // Consomme le \n

        Optional<Commande> commandeOpt = commandeService.getCommandeDetails(commandeId);

        if (commandeOpt.isEmpty()) {
            System.out.println("ERREUR: Commande non trouvée.");
            return;
        }

        Commande commande = commandeOpt.get();
        System.out.println("Détails pour Commande ID: " + commande.getId());
        System.out.println("Date: " + commande.getDateCommande());
        System.out.println("Montant Total: " + commande.getMontantTotal() + " FCFA");
        System.out.println("Produits commandés :");

        // Affichage des lignes de commande
        for (LigneCommande ligne : commande.getLignes()) {
            System.out.println(
                "  - Produit: " + ligne.getProduit().getName() +
                " (ID: " + ligne.getProduit().getId() + ")" +
                " | Quantité: " + ligne.getQuantite() +
                " | Prix Unit.: " + ligne.getPrixUnitaire() + " FCFA"
            );
        }
    }

    /**
     * Méthode d'aide pour afficher les produits (copiée de RsView).
     */
    private void afficheProduits(List<Produit> produits) {
       for (Produit produit : produits) {
           System.out.println(
               "  ID: " + produit.getId() + 
               " | Nom: " + produit.getName() +
               " | Stock: " + produit.getQteStock() +
               " | Prix: " + produit.getPu() + " FCFA"
           );
       }
   }
}