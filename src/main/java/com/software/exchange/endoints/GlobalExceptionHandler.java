package com.software.exchange.endoints;

import com.software.exchange.exceptions.DataNotPresentException;
import com.software.exchange.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    protected @ResponseBody Error  handleResourceNotFound(ResourceNotFoundException ex) {
        Error error = new Error();
        error.setTimestamp(LocalDateTime.now());
        error.setMessage(ex.getMessage());
        return error;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataNotPresentException.class)
    protected @ResponseBody Error handleDataNotPresent(DataNotPresentException ex) {
        Error error = new Error();
        error.setTimestamp(LocalDateTime.now());
        error.setMessage("Internal Server Error");
        LOGGER.error(ex.getMessage(), ex);
        return error;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected @ResponseBody Error handleGeneralException(Exception ex) {
        Error error = new Error();
        error.setTimestamp(LocalDateTime.now());
        error.setMessage("Internal Server Error");
        LOGGER.error(ex.getMessage(), ex);
        return error;
    }

    class Error {
        LocalDateTime timestamp;
        String message;

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
