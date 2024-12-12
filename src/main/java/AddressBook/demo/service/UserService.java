package AddressBook.demo.service;

import AddressBook.demo.dto.LoginDto;
import AddressBook.demo.dto.RegisterDto;
import AddressBook.demo.dto.UserDto;

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
