package com.wevolv.wevibeservice.domain.model;

import com.wevolv.wevibeservice.domain.model.enums.LocationTag;
import com.wevolv.wevibeservice.domain.model.enums.VibeTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vibeTags")
public class VibeTags {
    @Id
    private String id;
    private VibeTag tagName;
    private String vibeId;
    private String keycloakId;
}
