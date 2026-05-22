package com.medicalrecord.service.impl;

import com.medicalrecord.dto.binding.SickLeaveBindingModel;
import com.medicalrecord.dto.view.SickLeaveViewModel;
import com.medicalrecord.entity.Exam;
import com.medicalrecord.entity.SickLeave;
import com.medicalrecord.repository.SickLeaveRepository;
import com.medicalrecord.service.ExamService;
import com.medicalrecord.service.SickLeaveService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SickLeaveServiceImpl implements SickLeaveService {

    private final SickLeaveRepository sickLeaveRepository;
    private final ExamService examService;
    private final ModelMapper modelMapper;

    public SickLeaveServiceImpl(SickLeaveRepository sickLeaveRepository, ExamService examService, ModelMapper modelMapper) {
        this.sickLeaveRepository = sickLeaveRepository;
        this.examService = examService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void issueSickLeave(SickLeaveBindingModel bindingModel) {
        Exam exam = examService.findEntityById(bindingModel.getExamId());

        if (exam.getSickLeave() != null) {
            throw new IllegalArgumentException("This exam already has an associated sick leave.");
        }

        SickLeave sickLeave = modelMapper.map(bindingModel, SickLeave.class);
        sickLeave.setExam(exam);

        sickLeaveRepository.save(sickLeave);
    }

    @Override
    public List<SickLeaveViewModel> getAllSickLeaves() {
        return sickLeaveRepository.findAll().stream()
                .map(sl -> modelMapper.map(sl, SickLeaveViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSickLeave(Long id) {
        sickLeaveRepository.deleteById(id);
    }
}
