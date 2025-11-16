package ism.mae.repository;

import java.util.Optional;

import ism.mae.entity.User;

public interface UserRepository {
   Optional<User> findUserByLoginAndPassword(String login,String password);
}