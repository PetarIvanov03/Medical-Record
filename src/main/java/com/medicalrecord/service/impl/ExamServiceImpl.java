package com.medicalrecord.service.impl;

import com.medicalrecord.dto.binding.ExamBindingModel;
import com.medicalrecord.dto.view.ExamViewModel;
import com.medicalrecord.entity.Doctor;
import com.medicalrecord.entity.Exam;
import com.medicalrecord.entity.Patient;
import com.medicalrecord.entity.UserEntity;
import com.medicalrecord.repository.ExamRepository;
import com.medicalrecord.service.DoctorService;
import com.medicalrecord.service.ExamService;
import com.medicalrecord.service.PatientService;
import com.medicalrecord.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public ExamServiceImpl(ExamRepository examRepository, PatientService patientService, DoctorService doctorService, UserService userService, ModelMapper modelMapper) {
        this.examRepository = examRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    private Doctor getCurrentDoctor(String username) {
        UserEntity user = userService.findByUsername(username);
        return doctorService.findEntityById(user.getId());
    }

    @Override
    public void addExam(ExamBindingModel bindingModel, String currentUsername) {
        Doctor doctor = getCurrentDoctor(currentUsername);
        Patient patient = patientService.findEntityById(bindingModel.getPatientId());

        Exam exam = modelMapper.map(bindingModel, Exam.class);
        exam.setDoctor(doctor);
        exam.setPatient(patient);

        // Business Rule: If insured, NZOK pays (we keep price as 0 for patient out-of-pocket tracking)
        // Alternatively, store the price and use the flag in reporting. We'll store the price and handle logic in reports (as done in repo)
        exam.setPrice(bindingModel.getPrice());

        examRepository.save(exam);
    }

    @Override
    public List<ExamViewModel> getAllExams() {
        return examRepository.findAll().stream()
                .map(exam -> modelMapper.map(exam, ExamViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public ExamViewModel getExamById(Long id) {
        return modelMapper.map(findEntityById(id), ExamViewModel.class);
    }

    @Override
    public Exam findEntityById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found"));
    }

    @Override
    public void updateExam(Long id, ExamBindingModel bindingModel, String currentUsername) {
        Exam exam = findEntityById(id);
        Doctor currentDoctor = getCurrentDoctor(currentUsername);

        if (!exam.getDoctor().getId().equals(currentDoctor.getId())) {
            throw new IllegalArgumentException("You can only edit exams you conducted.");
        }

        Patient patient = patientService.findEntityById(bindingModel.getPatientId());

        exam.setDate(bindingModel.getDate());
        exam.setPatient(patient);
        exam.setDiagnosis(bindingModel.getDiagnosis());
        exam.setTreatment(bindingModel.getTreatment());
        exam.setPrice(bindingModel.getPrice());

        examRepository.save(exam);
    }

    @Override
    public void deleteExam(Long id, String currentUsername) {
        Exam exam = findEntityById(id);
        UserEntity user = userService.findByUsername(currentUsername);

        // Admin can delete any, Doctor only their own
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getRoleName().name().equals("ADMIN"));
        if (!isAdmin) {
            Doctor currentDoctor = doctorService.findEntityById(user.getId());
            if (!exam.getDoctor().getId().equals(currentDoctor.getId())) {
                throw new IllegalArgumentException("You can only delete exams you conducted.");
            }
        }

        examRepository.deleteById(id);
    }
}
