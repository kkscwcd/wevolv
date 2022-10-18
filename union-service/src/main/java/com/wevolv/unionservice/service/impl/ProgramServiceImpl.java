package com.wevolv.unionservice.service.impl;

import com.wevolv.unionservice.exceptions.NotFoundException;
import com.wevolv.unionservice.integration.profile.service.ProfileService;
import com.wevolv.unionservice.model.Member;
import com.wevolv.unionservice.model.Program;
import com.wevolv.unionservice.model.dto.ProgramDto;
import com.wevolv.unionservice.repository.ProgramRepository;
import com.wevolv.unionservice.service.ProgramService;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository programRepository;
    private final ProfileService profileService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    public ProgramServiceImpl(ProgramRepository programRepository, ProfileService profileService) {
        this.programRepository = programRepository;
        this.profileService = profileService;
    }

    @Override
    public Program createNewProgram(ProgramDto programDto) {
        Program newProgram = Program.builder()
                .id(UUID.randomUUID().toString())
                .title(programDto.getTitle())
                .description(programDto.getDescription())
                .startDate(LocalDate.parse(programDto.getStartDate(), formatter))
                .duration(programDto.getDuration())
                .externalProgramInfo(programDto.getExternalProgramInfo())
                .contactPerson(programDto.getContactPerson())
                .build();

        programRepository.save(newProgram);
        return newProgram;
    }

    @Override
    public void deleteProgram(String programId) {programRepository.deleteById(programId);}

    @Override
    public Program updateProgram(ProgramDto programDto, String programId) {
        var program = programRepository.findById(programId)
                .orElseThrow(() -> new NotFoundException(String.format("Program with programId %s doesn't exist", programId)));
        program.setTitle(programDto.getTitle());
        program.setDescription(programDto.getDescription());
        program.setStartDate(LocalDate.parse(programDto.getStartDate(), formatter));
        program.setDuration(programDto.getDuration());
        program.setExternalProgramInfo(programDto.getExternalProgramInfo());
        program.setContactPerson(programDto.getContactPerson());

        programRepository.save(program);

        return program;
    }

    @Override
    public List<Program> getAllPrograms() {return programRepository.findAll();}

    @Override
    public Program getProgramById(String programId) {
        return programRepository.findById(programId)
                .orElseThrow(() -> new NotFoundException(String.format("Program with programId %s doesn't exist", programId)));
    }
}
