package ism.mae;

import ism.mae.config.Database;
import ism.mae.entity.RoleEnum;
import ism.mae.entity.User;
import ism.mae.repository.CategorieRepository;
import ism.mae.repository.CommandeRepository;
import ism.mae.repository.ProduitRepository;
import ism.mae.repository.UserRepository;
import ism.mae.repository.impl.CategorieRepositoryImpl;
import ism.mae.repository.impl.CommandeRepositoryImpl;
import ism.mae.repository.impl.ProduitRepositoryImpl;
import ism.mae.repository.impl.UserRepositoryImpl;
import ism.mae.service.CategorieService;
import ism.mae.service.CommandeService;
import ism.mae.service.ProduitService;
import ism.mae.service.UserService;
import ism.mae.service.impl.CategorieServiceImpl;
import ism.mae.service.impl.CommandeServiceImpl;
import ism.mae.service.impl.ProduitServiceImpl;
import ism.mae.service.impl.UserServiceImpl;
import ism.mae.views.AuthView;
import ism.mae.views.BoutiquierView; // <-- 1. Importer la nouvelle vue
import ism.mae.views.RsView;

public class Main {
    public static void main(String[] args) {

          // === Phase d'Initialisation (Injection de Dépendances) ===

          // 1. Couche Repository (accès aux données)
          UserRepository userRepository = new UserRepositoryImpl();
          CategorieRepository categorieRepository = new CategorieRepositoryImpl();
          ProduitRepository produitRepository = new ProduitRepositoryImpl();
          CommandeRepository commandeRepository = new CommandeRepositoryImpl();

          // 2. Couche Service (logique métier)
          UserService userService = new UserServiceImpl(userRepository);
          CategorieService categorieService = new CategorieServiceImpl(categorieRepository);
          ProduitService produitService = new ProduitServiceImpl(produitRepository);
          CommandeService commandeService = new CommandeServiceImpl(commandeRepository, produitRepository);
         
          // === Phase de Démarrage ===

          // 1. Connexion
          AuthView authView = new AuthView(userService);
          User user = authView.connexion();
            
          System.out.println("\nConnexion réussie ! Bienvenue : " + user.getName());
          RoleEnum roleUserConnect = user.getRole();

          // 2. Routage vers la bonne vue (selon le rôle)
          switch (roleUserConnect) {
              case RESPONSABLE_STOCK:
                  // Injecter les services nécessaires au RS
                  RsView rsView = new RsView(categorieService, produitService); 
                  rsView.main();
                  break;
          
              case BOUTIQUIER:
                  // 2. Injecter les services nécessaires au Boutiquier
                  BoutiquierView boutiquierView = new BoutiquierView(commandeService, produitService);
                  boutiquierView.main(); // Lancer la vue Boutiquier
                  break;

              default:
                  break;
          }

          // === Phase de Fin ===
          Database.closeConnection(); // Ferme la connexion BDD
          System.out.println("Programme terminé.");
    }
}