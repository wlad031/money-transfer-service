package org.wlad031.money.transfer.exception;

public class InvalidAccountNameException extends ValidationException {

    public InvalidAccountNameException(String name) {
       super("Invalid account name: " + (name == null ? "null" : name));
    }
}
