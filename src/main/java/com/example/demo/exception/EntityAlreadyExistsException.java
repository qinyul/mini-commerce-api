package com.example.demo.exception;

public class EntityAlreadyExistsException extends IllegalArgumentException {
    public EntityAlreadyExistsException(String messsage) {
        super(messsage);
    }
}
