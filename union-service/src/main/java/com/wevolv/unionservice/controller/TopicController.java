package com.wevolv.unionservice.controller;

import com.wevolv.unionservice.model.Topic;
import com.wevolv.unionservice.model.UserDataContext;
import com.wevolv.unionservice.model.dto.ObjectCreatedResponse;
import com.wevolv.unionservice.model.dto.ObjectDeletedResponse;
import com.wevolv.unionservice.model.dto.TopicDto;
import com.wevolv.unionservice.model.dto.TopicPublishedResponse;
import com.wevolv.unionservice.service.TopicService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.wevolv.unionservice.util.TokenDecoder.getUserIdFromToken;

@RestController
@RequestMapping("/topic")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping(value = "/create")
    public ObjectCreatedResponse createNewTopic(HttpServletRequest request, @RequestBody TopicDto topicDto) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var topic = topicService.createNewTopic(keycloakId, topicDto);
        return new ObjectCreatedResponse(topic.getId());
    }

    @PostMapping(value = "/publish/forum/{topicId}")
    public TopicPublishedResponse publishTopicToForum(HttpServletRequest request, @PathVariable String topicId) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var udc = UserDataContext.create(keycloakId, jwt);
        topicService.publishTopicToForum(udc, topicId);
        return new TopicPublishedResponse(true);
    }

    @DeleteMapping(value = "/delete/{topicId}")
    public ObjectDeletedResponse deleteTopic(HttpServletRequest request, @PathVariable String topicId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var udc = UserDataContext.create(keycloakId, jwt);
        topicService.deleteTopic(udc, topicId);
        return new ObjectDeletedResponse(true);
    }

    @PostMapping(value = "/update/{topicId}")
    public Topic updateTopic(HttpServletRequest request, @RequestBody TopicDto topicDto, @PathVariable String topicId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var udc = UserDataContext.create(keycloakId, jwt);
        return topicService.updateTopic(topicDto, udc, topicId);
    }

    @GetMapping(value = "/all")
    public Map<String, Object> getAllTopics(HttpServletRequest request,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "3") int size){

        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        return topicService.getAllTopics(paging);
    }

    @GetMapping(value = "/{topicId}")
    public Topic getTopicById(HttpServletRequest request, @PathVariable String topicId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        return topicService.getTopicById(keycloakId, topicId);
    }

    @GetMapping(value = "/search/{title}")
    public List<Topic> searchTopicByTitle(@PathVariable String title){
        return topicService.searchTopicByTitle(title);
    }
}
