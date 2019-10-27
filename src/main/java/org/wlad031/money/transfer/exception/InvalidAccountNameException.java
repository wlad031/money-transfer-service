package org.wlad031.money.transfer.exception;

public class InvalidAccountNameException extends RuntimeException {

    private final String name;

    public InvalidAccountNameException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Invalid account name: " + (name == null ? "null" : name);
    }
}
