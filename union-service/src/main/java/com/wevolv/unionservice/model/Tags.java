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
@Builder
public class Tags {
    @Id
    private String id;
    private String topicId;
    private String postId;
    private Tag tagName;
}
