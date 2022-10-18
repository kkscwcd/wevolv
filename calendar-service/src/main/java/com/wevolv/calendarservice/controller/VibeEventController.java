package com.wevolv.calendarservice.controller;

import com.wevolv.calendarservice.model.Vibe;
import com.wevolv.calendarservice.model.dto.VibeDto;
import com.wevolv.calendarservice.service.VibeService;
import com.wevolv.calendarservice.util.TokenDecoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.wevolv.calendarservice.util.TokenDecoder.getUserIdFromToken;

@RestController
public class VibeEventController {
    private final VibeService vibeService;

    public VibeEventController(VibeService vibeService) {
        this.vibeService = vibeService;
    }

    @PostMapping("/save")
    public Vibe saveVibe(@RequestBody VibeDto vibeDto, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        return vibeService.saveVibe(vibeDto, keycloakId);
    }

    @GetMapping(value = "/{vibeId}")
    public Vibe getVibeById(@PathVariable String vibeId, HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = getUserIdFromToken(jwt);

        return vibeService.getVibeById(vibeId, keycloakId);
    }

    @GetMapping(value = "/all/vibes")
    public List<Vibe> getAllVibes(HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = getUserIdFromToken(jwt);

        return vibeService.getAllVibes(keycloakId);

    }

    @DeleteMapping ("/delete/{vibeId}")
    public void deleteVibe(@PathVariable String vibeId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        vibeService.deleteVibe(vibeId);
    }
}
