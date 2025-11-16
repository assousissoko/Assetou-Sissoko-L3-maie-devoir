package ism.mae.service.impl;

import java.util.List;
import java.util.Optional;

import ism.mae.entity.Categorie;
import ism.mae.repository.CategorieRepository;
import ism.mae.service.CategorieService;

public class CategorieServiceImpl implements CategorieService {
    //Couplage Faible ==> OCP
      private CategorieRepository categorieRepository;
    
    //Dependency Injection (Injection de dépendance par constructeur)
      public CategorieServiceImpl(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
      }

      @Override
      public boolean addCategorie(Categorie categorie) {
        return categorieRepository.insertCategorie(categorie);
      }
      
      @Override
      public Optional<Categorie> getCategorieByName(String name) {
          return categorieRepository.findCategorieByName(name);
      }

      @Override
      public List<Categorie> getAllCategories() {
         return categorieRepository.findAllCategories();
      }
}