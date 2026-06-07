package com.ivanovp.medical_record.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/not-found")
        public void notFound() {
            throw new ResourceNotFoundException("Item not found with id: 99");
        }

        @GetMapping("/access-denied")
        public void accessDenied() {
            throw new AccessDeniedException("Access denied to this resource");
        }

        @GetMapping("/illegal-argument")
        public void illegalArgument() {
            throw new IllegalArgumentException("Invalid argument provided");
        }

        @GetMapping("/illegal-state")
        public void illegalState() {
            throw new IllegalStateException("Operation not permitted in current state");
        }

        @GetMapping("/data-integrity")
        public void dataIntegrity() {
            throw new DataIntegrityViolationException("Database constraint violation");
        }

        @GetMapping("/auth-error")
        public void authError() {
            throw new BadCredentialsException("Invalid username or password");
        }

        @GetMapping("/unexpected-error")
        public void unexpectedError() {
            throw new RuntimeException("Something went wrong");
        }

        @PostMapping("/validation")
        public void validation(@Valid @RequestBody ValidationTestDTO dto) {
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class ValidationTestDTO {
            @NotBlank(message = "Name is required")
            private String name;
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleResourceNotFoundException_returns404WithMessage() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Item not found with id: 99"));
    }

    @Test
    void handleAccessDeniedException_returns403WithMessage() throws Exception {
        mockMvc.perform(get("/test/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied to this resource"));
    }

    @Test
    void handleIllegalArgumentException_returns400WithMessage() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid argument provided"));
    }

    @Test
    void handleIllegalStateException_returns409WithMessage() throws Exception {
        mockMvc.perform(get("/test/illegal-state"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Operation not permitted in current state"));
    }

    @Test
    void handleDataIntegrityViolationException_returns409WithGenericMessage() throws Exception {
        mockMvc.perform(get("/test/data-integrity"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Operation rejected: a database constraint was violated"));
    }

    @Test
    void handleAuthenticationException_returns401WithMessage() throws Exception {
        mockMvc.perform(get("/test/auth-error"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void handleGenericException_returns500WithGenericMessage() throws Exception {
        mockMvc.perform(get("/test/unexpected-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }

    @Test
    void handleMethodArgumentNotValidException_returns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name is required"));
    }
}
