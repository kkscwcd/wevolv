package com.wevolv.wevibeservice.domain.model;

import com.wevolv.wevibeservice.domain.model.enums.LocationTag;
import com.wevolv.wevibeservice.domain.model.enums.VibeTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DefaultTagsLocation {
    @Id
    private String id;
    private LocationTag tagName;
}
