package com.wevolv.calendarservice.service;

import com.wevolv.calendarservice.model.Vibe;
import com.wevolv.calendarservice.model.dto.VibeDto;

import java.util.List;

public interface VibeService {
    Vibe saveVibe(VibeDto vibeDto, String keycloakId);

    Vibe getVibeById(String vibeId, String keycloakId);

    List<Vibe> getAllVibes(String keycloakId);

    void deleteVibe(String vibeId);
}
