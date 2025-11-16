package ism.mae.service;

import java.util.List;
import java.util.Optional;

import ism.mae.entity.Categorie;

public interface CategorieService {
     boolean addCategorie(Categorie categorie);
     Optional<Categorie> getCategorieByName(String name);
     List<Categorie> getAllCategories();
}