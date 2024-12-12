package AddressBook.demo.service;

import AddressBook.demo.Entity.User;
import AddressBook.demo.Exception.*;
import AddressBook.demo.Repository.UserRepository;
import AddressBook.demo.Util.Emailutil;
import AddressBook.demo.Util.OtpUtil;
import AddressBook.demo.dto.LoginDto;
import AddressBook.demo.dto.RegisterDto;
import AddressBook.demo.dto.UserDto;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpUtil otpUtil;

    @Autowired
    private Emailutil emailUtil;

    @Override
    public UserDto createUser(UserDto userDto) {
        if(userRepository.findByEmail(userDto.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email is already in use!!");
        }
        if (!userDto.getEmail().matches(EMAIL_PATTERN)) {
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

    public void register(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists with this email: " + registerDto.getEmail());
        }

        if (!Pattern.matches(EMAIL_PATTERN, registerDto.getEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + registerDto.getEmail());
        }

        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(registerDto.getEmail(), otp);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send OTP, please try again");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPhoneno(registerDto.getPhoneno());
        user.setGender(registerDto.getGender());
        user.setDob(registerDto.getDob());
        user.setAddress(registerDto.getAddress());
        user.setPassword(registerDto.getPassword());
        user.setConfirmPassword(registerDto.getConfirmPassword());
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
    }

    public void verifyAccount(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));


        if (!user.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP.");
        }

        if (Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() >= (1 * 60)) {
            throw new IllegalArgumentException("OTP has expired. Please regenerate.");
        }


        user.setActive(true);
        userRepository.save(user);
    }


    public void regenerateOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));

        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send OTP, please try again");
        }

        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

    }

    @Override
    public void login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new EmailNotFoundException("User not found with this email: " + loginDto.getEmail()));

        // Check if the password matches
        if (!loginDto.getPassword().equals(user.getPassword())) {
            throw new ResultNotFoundException("Password does not match!!");
        }

    }


}
