package ism.mae.repository.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ism.mae.config.Database;
import ism.mae.entity.Categorie;
import ism.mae.entity.Commande;
import ism.mae.entity.LigneCommande;
import ism.mae.entity.Produit;
import ism.mae.repository.CommandeRepository;

public class CommandeRepositoryImpl implements CommandeRepository {

    // Définition des requêtes SQL
    private final String SQL_INSERT_CMD = "INSERT INTO commandes (dateCommande, montantTotal) VALUES (?, ?)";
    private final String SQL_INSERT_LIGNE = "INSERT INTO ligne_commandes (commande_id, produit_id, quantite, prixUnitaire) VALUES (?, ?, ?, ?)";
    private final String SQL_UPDATE_STOCK = "UPDATE produits SET qteStock = qteStock - ? WHERE id = ?";

    private final String SQL_SELECT_ALL_CMD = "SELECT * FROM commandes ORDER BY dateCommande DESC";
    
    // Jointure complexe pour récupérer tous les détails d'une commande
    private final String SQL_SELECT_DETAILS_BY_ID = "SELECT " +
            "c.id as cmd_id, c.dateCommande, c.montantTotal, " +
            "l.id as ligne_id, l.quantite, l.prixUnitaire, " +
            "p.id as prod_id, p.name as prod_name, p.pu as prod_pu, p.qteStock, " +
            "cat.id as cat_id, cat.name as cat_name " +
            "FROM commandes c " +
            "JOIN ligne_commandes l ON c.id = l.commande_id " +
            "JOIN produits p ON l.produit_id = p.id " +
            "LEFT JOIN categories cat ON p.categorie_id = cat.id " +
            "WHERE c.id = ?";

    @Override
    public Commande insertCommande(Commande commande) {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            // 1. DÉMARRER LA TRANSACTION
            // (SOLID - Atomicité)
            conn.setAutoCommit(false);

            // 2. Insérer la Commande (table 'commandes')
            try (PreparedStatement psCmd = conn.prepareStatement(SQL_INSERT_CMD, Statement.RETURN_GENERATED_KEYS)) {
                psCmd.setDate(1, Date.valueOf(commande.getDateCommande()));
                psCmd.setDouble(2, commande.getMontantTotal());
                psCmd.executeUpdate();

                ResultSet rs = psCmd.getGeneratedKeys();
                if (rs.next()) {
                    commande.setId(rs.getInt(1)); // Récupère l'ID de la commande
                }
            }

            // 3. Insérer les Lignes de Commande et Mettre à jour le Stock
            try (PreparedStatement psLigne = conn.prepareStatement(SQL_INSERT_LIGNE);
                 PreparedStatement psStock = conn.prepareStatement(SQL_UPDATE_STOCK)) {

                for (LigneCommande ligne : commande.getLignes()) {
                    // 3a. Insérer la ligne (table 'ligne_commandes')
                    psLigne.setInt(1, commande.getId());
                    psLigne.setInt(2, ligne.getProduit().getId());
                    psLigne.setDouble(3, ligne.getQuantite());
                    psLigne.setDouble(4, ligne.getPrixUnitaire());
                    psLigne.addBatch(); // Ajoute à un lot d'exécution

                    // 3b. Préparer la mise à jour du stock (table 'produits')
                    psStock.setDouble(1, ligne.getQuantite());
                    psStock.setInt(2, ligne.getProduit().getId());
                    psStock.addBatch();
                }
                psLigne.executeBatch(); // Exécute toutes les insertions de lignes
                psStock.executeBatch(); // Exécute toutes les mises à jour de stock
            }

            // 4. VALIDER LA TRANSACTION
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                // 5. ANNULER LA TRANSACTION (Rollback) en cas d'erreur
                if (conn != null) conn.rollback();
            } catch (SQLException eRollback) {
                eRollback.printStackTrace();
            }
        } finally {
            try {
                // Rétablir le mode auto-commit
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException eAutoCommit) {
                eAutoCommit.printStackTrace();
            }
        }
        return commande;
    }

    @Override
    public List<Commande> findAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL_CMD);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                commandes.add(Commande.builder()
                        .id(rs.getInt("id"))
                        .dateCommande(rs.getDate("dateCommande").toLocalDate())
                        .montantTotal(rs.getDouble("montantTotal"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }

    @Override
    public Optional<Commande> findById(int commandeId) {
        Commande commande = null;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_DETAILS_BY_ID)) {
            
            ps.setInt(1, commandeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // La première ligne construit la commande
                if (commande == null) {
                    commande = Commande.builder()
                            .id(rs.getInt("cmd_id"))
                            .dateCommande(rs.getDate("dateCommande").toLocalDate())
                            .montantTotal(rs.getDouble("montantTotal"))
                            .build();
                }
                
                // Construit la catégorie du produit
                Categorie categorie = Categorie.builder()
                        .id(rs.getInt("cat_id"))
                        .name(rs.getString("cat_name"))
                        .build();

                // Construit le produit de la ligne
                Produit produit = Produit.builder()
                        .id(rs.getInt("prod_id"))
                        .name(rs.getString("prod_name"))
                        .pu(rs.getDouble("prod_pu"))
                        .qteStock(rs.getDouble("qteStock"))
                        .categorie(categorie)
                        .build();

                // Construit la ligne de commande
                LigneCommande ligne = LigneCommande.builder()
                        .id(rs.getInt("ligne_id"))
                        .quantite(rs.getDouble("quantite"))
                        .prixUnitaire(rs.getDouble("prixUnitaire"))
                        .produit(produit)
                        .commande(commande) // Lie la ligne à la commande
                        .build();
                
                // Ajoute la ligne à la commande
                commande.getLignes().add(ligne);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(commande);
    }
}