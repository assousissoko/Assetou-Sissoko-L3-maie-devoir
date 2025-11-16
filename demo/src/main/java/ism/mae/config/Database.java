package ism.mae.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // 1. PARAMÈTRES DE CONNEXION
    // Assure-toi qu'ils correspondent à ton WAMP (BDD, user, mot de passe)
    private static final String URL = "jdbc:mysql://localhost:3306/gescom_java";
    private static final String USER = "root";
    // WAMP utilise souvent un mot de passe vide ("")
    private static final String PASSWORD = ""; 

    private static Connection connection = null;

    // 2. BLOC STATIC (chargé une seule fois au démarrage)
    // Charge le driver MySQL
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur critique : Driver JDBC MySQL introuvable.");
            e.printStackTrace();
        }
    }

    /**
     * Fournit une instance unique (Singleton) de la connexion à la BDD.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Ouvre la connexion si elle n'existe pas ou est fermée
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Ferme la connexion globale à la fin du programme.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la BDD fermée.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}