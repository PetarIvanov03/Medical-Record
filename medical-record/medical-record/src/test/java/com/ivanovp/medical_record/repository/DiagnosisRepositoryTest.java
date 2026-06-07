package com.ivanovp.medical_record.repository;

import com.ivanovp.medical_record.entity.Diagnosis;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DiagnosisRepositoryTest {

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Test
    void existsByCode_whenCodeExists_returnsTrue() {
        // Arrange
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode("J06.9");
        diagnosis.setName("Acute upper respiratory infection");
        diagnosisRepository.save(diagnosis);

        // Act
        boolean result = diagnosisRepository.existsByCode("J06.9");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void existsByCode_whenCodeDoesNotExist_returnsFalse() {
        // Act
        boolean result = diagnosisRepository.existsByCode("Z99.9");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void existsByCode_isCaseSensitive() {
        // Arrange
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode("A01.0");
        diagnosis.setName("Typhoid fever");
        diagnosisRepository.save(diagnosis);

        // Act
        boolean result = diagnosisRepository.existsByCode("a01.0");

        // Assert
        assertThat(result).isFalse();
    }
}
