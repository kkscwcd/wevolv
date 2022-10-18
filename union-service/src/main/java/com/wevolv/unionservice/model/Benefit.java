package com.wevolv.unionservice.model;

import com.wevolv.unionservice.model.enums.UnionProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Benefit {
    @Id
    private String id;
    private String title;
    private String description;
    private boolean isActive;
    private UnionProvider unionProvider;
}
