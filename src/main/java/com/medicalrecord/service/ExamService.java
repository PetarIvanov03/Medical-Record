package com.medicalrecord.service;

import com.medicalrecord.dto.binding.ExamBindingModel;
import com.medicalrecord.dto.view.ExamViewModel;
import com.medicalrecord.entity.Exam;

import java.util.List;

public interface ExamService {
    void addExam(ExamBindingModel bindingModel, String currentUsername);
    List<ExamViewModel> getAllExams();
    ExamViewModel getExamById(Long id);
    Exam findEntityById(Long id);
    void updateExam(Long id, ExamBindingModel bindingModel, String currentUsername);
    void deleteExam(Long id, String currentUsername);
}
