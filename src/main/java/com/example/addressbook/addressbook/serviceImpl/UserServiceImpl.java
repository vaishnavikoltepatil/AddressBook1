package com.example.addressbook.addressbook.serviceImpl;


import com.example.addressbook.addressbook.dto.UserDto;
import com.example.addressbook.addressbook.entity.User;
import com.example.addressbook.addressbook.exception.EmailAlreadyExistsException;
import com.example.addressbook.addressbook.exception.InvalidEmailFormatException;
import com.example.addressbook.addressbook.exception.UserAlreadyExistsException;
import com.example.addressbook.addressbook.exception.UserNotFoundException;
import com.example.addressbook.addressbook.repository.UserRepository;
import com.example.addressbook.addressbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;



    @Override
    public UserDto createUser(UserDto userDto) {
        if(userRepository.findByEmail(userDto.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email is already in use!!");
        }
        if (!userDto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidEmailFormatException("Invalid email format.");
        }

        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists.");
        }

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match.");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhoneno(userDto.getPhoneno());
        user.setGender(userDto.getGender());
        user.setDob(userDto.getDob());
        user.setAddress(userDto.getAddress());
        user.setPassword(userDto.getPassword());


        User savedUser = userRepository.save(user);


        return new UserDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getPhoneno(),
                savedUser.getGender(),
                savedUser.getDob(),
                savedUser.getAddress(),
                savedUser.getPassword(),
                savedUser.getPassword()
        );
    }
    @Override
    public UserDto updateUser(int id, UserDto userDto) throws UserNotFoundException {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match.");
        }


        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhoneno(userDto.getPhoneno());
        user.setGender(userDto.getGender());
        user.setDob(userDto.getDob());
        user.setAddress(userDto.getAddress());
        user.setPassword(userDto.getPassword());


        userRepository.save(user);


        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getPhoneno(),
                user.getGender(), user.getDob(), user.getAddress(), user.getPassword(), null);
    }


    @Override
    public void deleteUser(int id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public UserDto getUserById(int id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getPhoneno(), user.getGender(), user.getDob(), user.getAddress(), user.getPassword(), null);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getPhoneno(), user.getGender(), user.getDob(), user.getAddress(), user.getPassword(), null))
                .collect(Collectors.toList());
    }
}
