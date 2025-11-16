package ism.mae.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ism.mae.config.Database;
import ism.mae.entity.Categorie;
import ism.mae.entity.Produit;
import ism.mae.repository.ProduitRepository;

public class ProduitRepositoryImpl implements ProduitRepository {

    // Définition des requêtes SQL
    private final String SQL_INSERT = "INSERT INTO produits (name, qteStock, pu, categorie_id) VALUES (?, ?, ?, ?)";
    private final String SQL_UPDATE_STOCK = "UPDATE produits SET qteStock = ? WHERE id = ?";
    
    // Jointure pour récupérer la catégorie avec le produit
    private final String SQL_SELECT_ALL = "SELECT p.*, c.name as categorie_name FROM produits p " +
                                          "LEFT JOIN categories c ON p.categorie_id = c.id";
                                          
    private final String SQL_SELECT_BY_CAT = SQL_SELECT_ALL + " WHERE p.categorie_id = ?";
    private final String SQL_SELECT_BY_ID = SQL_SELECT_ALL + " WHERE p.id = ?";


    @Override
    public Produit insertProduit(Produit produit) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, produit.getName());
            ps.setDouble(2, produit.getQteStock());
            ps.setDouble(3, produit.getPu());
            ps.setInt(4, produit.getCategorie().getId());
            
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                produit.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produit;
    }

    @Override
    public Produit updateStock(Produit produit) {
         try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STOCK)) {
            
            ps.setDouble(1, produit.getQteStock());
            ps.setInt(2, produit.getId());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produit;
    }

    @Override
    public List<Produit> findAllProduits() {
        List<Produit> produits = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                produits.add(buildProduitFromResultSet(rs)); // Factorisation
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    @Override
    public List<Produit> findProduitsByCategorieId(int categorieId) {
        List<Produit> produits = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_CAT)) {
            
            ps.setInt(1, categorieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    produits.add(buildProduitFromResultSet(rs)); // Factorisation
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    @Override
    public Optional<Produit> findById(int id) {
         try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(buildProduitFromResultSet(rs)); // Factorisation
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Méthode privée (helper) pour construire un objet Produit depuis un ResultSet.
     * Évite la duplication de code (Principe DRY).
     */
    private Produit buildProduitFromResultSet(ResultSet rs) throws SQLException {
        // Construit la catégorie associée (peut être null si categorie_id est null)
        Categorie categorie = Categorie.builder()
                .id(rs.getInt("categorie_id"))
                .name(rs.getString("categorie_name"))
                .build();
        
        // Construit le produit
        return Produit.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .qteStock(rs.getDouble("qteStock"))
                .pu(rs.getDouble("pu"))
                .categorie(categorie)
                .build();
    }
}