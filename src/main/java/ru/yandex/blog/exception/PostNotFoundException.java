package ru.yandex.blog.exception;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(String message) {
        super(message);
    }
}


