package com.software.exchangerate.exceptions;

public class DataNotPresentException extends RuntimeException{
    public DataNotPresentException(String msg){
        super(msg);
    }
}
