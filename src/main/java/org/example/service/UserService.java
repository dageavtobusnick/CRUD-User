package org.example.service;

import org.example.dao.UserDao;
import org.example.dao.UserDaoPostgreSQL;
import org.example.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDaoPostgreSQL();
    }

    public User createUser(String name, String email, Integer age) {
        logger.info("Creating new user: {}", email);

        Optional<User> existingUser = userDao.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        User user = new User(name, email, age);
        return userDao.save(user);
    }

    public Optional<User> getUserById(Long id) {
        logger.info("Retrieving user by id: {}", id);
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        logger.info("Retrieving all users");
        return userDao.findAll();
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        logger.info("Updating user: {}", id);

        User user = userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (!user.getEmail().equals(email)) {
            Optional<User> existingUser = userDao.findByEmail(email);
            if (existingUser.isPresent()) {
                throw new IllegalArgumentException("User with email " + email + " already exists");
            }
        }

        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        return userDao.update(user);
    }

    public void deleteUser(Long id) {
        logger.info("Deleting user: {}", id);
        userDao.delete(id);
    }
}