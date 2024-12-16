package com.example.addressbook.addressbook.service;



import com.example.addressbook.addressbook.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(int id,UserDto userDto);

    void deleteUser(int id);

    UserDto getUserById(int id);

    List<UserDto> getAllUsers();



}
