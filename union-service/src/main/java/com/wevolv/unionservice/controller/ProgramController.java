package com.wevolv.unionservice.controller;

import com.wevolv.unionservice.model.GenericApiResponse;
import com.wevolv.unionservice.model.Program;
import com.wevolv.unionservice.model.dto.ObjectCreatedResponse;
import com.wevolv.unionservice.model.dto.ObjectDeletedResponse;
import com.wevolv.unionservice.model.dto.ProgramDto;
import com.wevolv.unionservice.service.ProgramService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.wevolv.unionservice.util.TokenDecoder.getUserIdFromToken;

@RestController
@RequestMapping("/program")
public class ProgramController {

    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }


    @PostMapping(value = "/create")
    public ObjectCreatedResponse createNewProgram(HttpServletRequest request, @RequestBody ProgramDto programDto) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var program = programService.createNewProgram(programDto);
        return new ObjectCreatedResponse(program.getId());
    }

    @DeleteMapping(value = "/delete/{programId}")
    public ObjectDeletedResponse deleteProgram(HttpServletRequest request, @PathVariable String programId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        programService.deleteProgram(programId);
        return new ObjectDeletedResponse(true);

    }

    @PostMapping(value = "/update/{programId}")
    public Program updateProgram(HttpServletRequest request, @RequestBody ProgramDto programDto, @PathVariable String programId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        return programService.updateProgram(programDto, programId);

    }

    @GetMapping(value = "/all")
    public ResponseEntity<GenericApiResponse> getAllPrograms(HttpServletRequest request){

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var programs = programService.getAllPrograms();
        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(programs)
                .message(String.format("All programs for union are here!"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(value = "/{programId}")
    public Program getProgramById(HttpServletRequest request, @PathVariable String programId){

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        return programService.getProgramById(programId);
    }
}
