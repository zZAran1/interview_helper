package com.example.interview_helper_backend.model.dto;

import lombok.Data;

@Data
public class ResumeAllDTO {
    ResumeDTO resumeDTO;
    ResumeEducationDTO resumeEducationDTO;
    ResumeProjectDTO resumeProjectDTO;
    ResumeWorkDTO resumeWorkDTO;
}
