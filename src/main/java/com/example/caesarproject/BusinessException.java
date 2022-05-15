package com.example.caesarproject;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
    public static class CharValidException  extends BusinessException {
        public CharValidException(String message) {
            super(message);
        }
    }

    public static class MyFileEmpty extends BusinessException{
        public MyFileEmpty(String message) {
            super(message);
        }
    }

    public static class MyFileNotFoundException extends BusinessException {
        public MyFileNotFoundException(String message) {
            super(message);
        }
    }
}
