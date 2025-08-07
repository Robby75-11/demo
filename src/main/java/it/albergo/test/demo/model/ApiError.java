package it.albergo.test.demo.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private LocalDateTime dataErrore;
    private int status;
    private String error;
    private String message;
}
