package com.example.addressbook.addressbook.service;



import com.example.addressbook.addressbook.dto.LoginDto;
import com.example.addressbook.addressbook.dto.RegisterDto;
import com.example.addressbook.addressbook.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(int id,UserDto userDto);

    void deleteUser(int id);

    UserDto getUserById(int id);

    List<UserDto> getAllUsers();

    void register(RegisterDto registerDto);

    void verifyAccount(String email, String otp);

    void regenerateOtp(String email);

    void login(LoginDto loginDto);


}
