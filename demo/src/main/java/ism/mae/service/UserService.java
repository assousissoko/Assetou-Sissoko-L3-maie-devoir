package ism.mae.service;

import java.util.Optional;

import ism.mae.entity.User;

public interface UserService {
   Optional<User> connection(String login,String password);
}