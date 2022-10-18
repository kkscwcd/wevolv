package com.wevolv.unionservice.model.dto;

import com.wevolv.unionservice.model.Post;
import com.wevolv.unionservice.model.Topic;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TopicToForumRequest {
    private Topic topic;
    private List<Post> posts;
}
