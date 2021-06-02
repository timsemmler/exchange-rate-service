package com.software.exchange.exceptions;

public class DataNotPresentException extends RuntimeException{
    public DataNotPresentException(String msg){
        super(msg);
    }
}
