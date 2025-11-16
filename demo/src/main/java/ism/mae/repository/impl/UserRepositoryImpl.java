package ism.mae.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import ism.mae.config.Database; // <-- 1. Importer notre classe de config
import ism.mae.entity.RoleEnum;
import ism.mae.entity.User;
import ism.mae.repository.UserRepository;

public class UserRepositoryImpl implements UserRepository {

    // Définir la requête SQL comme constante
    private final String SQL_SELECT_AUTH = "SELECT id, name, role FROM `users` WHERE `login` like ? and password like ?";

    @Override
    public Optional<User> findUserByLoginAndPassword(String login, String password) {
        
        // 2. Utiliser Database.getConnection() et un try-with-resources
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_AUTH)) {
            
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            User user = null;
            if (rs.next()) {
                // Conversion de la String de la BDD vers l'Enum Java
                RoleEnum role = rs.getString("role").equals("RESPONSABLE_STOCK") 
                                ? RoleEnum.RESPONSABLE_STOCK 
                                : RoleEnum.BOUTIQUIER;
                
                user = User.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .role(role)
                        // Important: Ne pas stocker le login/password dans l'objet User après connexion
                        .build();
            }
            // Gère le cas où l'utilisateur est null (non trouvé)
            return Optional.ofNullable(user); 

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'authentification de l'utilisateur");
            e.printStackTrace();
        }
        return Optional.empty();
    }
}