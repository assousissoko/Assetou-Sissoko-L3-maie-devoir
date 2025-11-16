package ism.mae.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ism.mae.config.Database; // <-- 1. Importer notre classe de config
import ism.mae.entity.Categorie;
import ism.mae.repository.CategorieRepository;

public class CategorieRepositoryImpl implements CategorieRepository {

    // Définir les requêtes SQL comme constantes (bonne pratique)
    private final String SQL_INSERT = "INSERT INTO `categories` (`name`) VALUES (?)";
    private final String SQL_SELECT_BY_NAME = "SELECT * FROM `categories` WHERE `name` like ?";
    private final String SQL_SELECT_ALL = "SELECT * FROM `categories`";

    @Override
    public boolean insertCategorie(Categorie categorie) {
        int result = 0;
        // 2. Utiliser Database.getConnection() et un try-with-resources
        // On ne gère plus Class.forName ou DriverManager ici
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, categorie.getName());
            result = ps.executeUpdate(); // Requete de MAJ

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result != 0;
    }

    @Override
    public Optional<Categorie> findCategorieByName(String name) {
        // 3. Utiliser Database.getConnection()
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_NAME)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery(); // Exécuter une requete Select

            if (rs.next()) {
                Categorie categorie = Categorie.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .build();
                return Optional.of(categorie); // Retourne la catégorie trouvée
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche de la catégorie par nom");
            e.printStackTrace();
        }
        return Optional.empty(); // Aucune catégorie trouvée
    }

    @Override
    public List<Categorie> findAllCategories() {
        List<Categorie> categories = new ArrayList<>();
        // 4. Utiliser Database.getConnection()
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(
                        Categorie.builder()
                                .id(rs.getInt("id"))
                                .name(rs.getString("name"))
                                .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}