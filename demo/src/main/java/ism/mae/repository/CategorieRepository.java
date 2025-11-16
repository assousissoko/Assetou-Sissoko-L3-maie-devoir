package ism.mae.repository;

import java.util.List;
import java.util.Optional;

import ism.mae.entity.Categorie;

public interface CategorieRepository {
     public boolean insertCategorie(Categorie categorie);
     Optional<Categorie> findCategorieByName(String name);
     List<Categorie> findAllCategories();
}