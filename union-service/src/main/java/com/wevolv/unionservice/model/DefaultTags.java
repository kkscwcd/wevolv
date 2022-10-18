package com.wevolv.unionservice.model;

import com.wevolv.unionservice.model.enums.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DefaultTags {
    @Id
    private String id;
    private Tag tagName;
}
