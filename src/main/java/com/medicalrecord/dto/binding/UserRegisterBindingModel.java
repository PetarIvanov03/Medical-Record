package com.medicalrecord.dto.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterBindingModel {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 3, message = "Password must be at least 3 characters")
    private String password;

    @NotBlank(message = "Confirm password cannot be blank")
    private String confirmPassword;

    @NotBlank(message = "Role must be selected")
    private String role;
}
