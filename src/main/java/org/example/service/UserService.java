package org.example.service;

import org.example.dto.CreateUserRequest;
import org.example.dto.UpdateUserRequest;
import org.example.dto.UserDto;
import org.example.mappers.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;

    @Autowired
    public UserService(UserRepository userRepository, UserEventPublisher userEventPublisher) {
        this.userRepository = userRepository;
        this.userEventPublisher = userEventPublisher;
    }

    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }

        User user = new User(request.getName(), request.getEmail(), request.getAge());
        User savedUser = userRepository.save(user);

        userEventPublisher.publishUserEvent("CREATE", savedUser.getEmail());

        return UserMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return UserMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());

        User updatedUser = userRepository.save(user);
        return UserMapper.toDto(updatedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        String userEmail = user.getEmail();
        userRepository.deleteById(id);

        // Отправка события в Kafka
        userEventPublisher.publishUserEvent("DELETE", userEmail);
    }

}
