package com.medicalrecord.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBusinessException(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorMsg", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("errorMsg", "An unexpected error occurred: " + ex.getMessage());
        return "error";
    }
}
