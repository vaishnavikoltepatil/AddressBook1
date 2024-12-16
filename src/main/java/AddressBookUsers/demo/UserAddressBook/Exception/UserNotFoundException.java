package AddressBookUsers.demo.UserAddressBook.Exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message){

        super(message);
    }
}
