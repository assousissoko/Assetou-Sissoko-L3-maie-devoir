package ism.mae.service.impl;

import java.util.Optional;

import ism.mae.entity.User;
import ism.mae.repository.UserRepository;
import ism.mae.service.UserService;

public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    
    //Injection de dépendance par constructeur
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> connection(String login, String password) {
       return userRepository.findUserByLoginAndPassword(login, password);
    }
}