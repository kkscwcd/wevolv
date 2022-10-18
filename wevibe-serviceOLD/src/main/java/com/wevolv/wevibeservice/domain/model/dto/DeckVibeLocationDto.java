package com.wevolv.wevibeservice.domain.model.dto;

import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.domain.model.Vibe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckVibeLocationDto {
    private List<String> vibeIds;
    private List<String> locationIds;
}
