
-- BASE DE DONNEES GESCOM JAVA - ASSETOU SISSOKO L3 MAIE

CREATE DATABASE IF NOT EXISTS gescom_java;
USE gescom_java;

DROP TABLE IF EXISTS ligne_commandes;
DROP TABLE IF EXISTS commandes;
DROP TABLE IF EXISTS produits;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS categories;

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('RESPONSABLE_STOCK', 'BOUTIQUIER') NOT NULL
) ENGINE=InnoDB;

CREATE TABLE produits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    qteStock DOUBLE NOT NULL DEFAULT 0,
    pu DOUBLE NOT NULL DEFAULT 0,
    categorie_id INT,
    FOREIGN KEY (categorie_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE commandes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dateCommande DATE NOT NULL,
    montantTotal DOUBLE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE ligne_commandes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quantite DOUBLE NOT NULL,
    prixUnitaire DOUBLE NOT NULL,
    commande_id INT,
    produit_id INT,
    FOREIGN KEY (commande_id) REFERENCES commandes(id) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE SET NULL
) ENGINE=InnoDB;

INSERT INTO users (name, login, password, role) VALUES
('Admin Stock', 'rs', 'rs', 'RESPONSABLE_STOCK'),
('Vendeur Boutique', 'bt', 'bt', 'BOUTIQUIER');

INSERT INTO categories (name) VALUES
('Alimentaire'),
('Boissons'),
('Électroménager');

INSERT INTO produits (name, qteStock, pu, categorie_id) VALUES
('Riz 50kg', 20, 18000, 1),
('Lait en poudre', 50, 2500, 1),
('Coca-Cola 1L', 30, 1000, 2),
('Ventilateur', 10, 15000, 3);
