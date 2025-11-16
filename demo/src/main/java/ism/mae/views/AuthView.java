package ism.mae.views;

import java.util.Optional;
import java.util.Scanner;

import ism.mae.entity.User;
import ism.mae.service.UserService;

public class AuthView {
     private  Scanner scanner=new Scanner(System.in);
     private UserService userService;

     public AuthView(UserService userService) {
        this.userService = userService;
     }

     public User connexion(){
        String login,pwd;
          Optional<User>  user;
          System.out.println("\n=== Écran de Connexion ===");
        do {
            do {
                // Utilise print() pour que l'utilisateur tape sur la même ligne
                System.out.print("Entrer le login: "); 
                login=scanner.nextLine();
            } while ( login.isBlank());
            
            do {
                // Utilise print() ici aussi
                System.out.print("Entrer le Mot de Passe: ");
                pwd=scanner.nextLine();
            } while ( pwd.isBlank());

           user =userService.connection(login, pwd);
            if (user.isEmpty()) {
               System.out.println("\nERREUR : Login ou Mot de passe Incorrect.\n");
             }
       } while (user.isEmpty());

       return user.get();
     }
}