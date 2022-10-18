package com.wevolv.unionservice.service;

import com.wevolv.unionservice.model.Program;
import com.wevolv.unionservice.model.dto.ProgramDto;

import java.util.List;

public interface ProgramService {
    Program createNewProgram(ProgramDto programDto);

    void deleteProgram(String programId);

    Program updateProgram(ProgramDto programDto, String programId);

    List<Program> getAllPrograms();

    Program getProgramById(String programId);
}
