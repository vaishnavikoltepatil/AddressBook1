package com.example.addressbook.addressbook.exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message){

        super(message);
    }
}
