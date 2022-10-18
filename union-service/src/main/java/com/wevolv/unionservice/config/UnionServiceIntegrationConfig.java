package com.wevolv.unionservice.config;

import com.wevolv.unionservice.integration.calendar.service.CalendarService;
import com.wevolv.unionservice.integration.calendar.service.impl.HttpCalendarServiceImpl;
import com.wevolv.unionservice.integration.forum.service.ForumService;
import com.wevolv.unionservice.integration.profile.service.ProfileService;
import com.wevolv.unionservice.repository.*;
import com.wevolv.unionservice.service.*;
import com.wevolv.unionservice.service.impl.*;
import com.wevolv.unionservice.util.CloudinaryHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class UnionServiceIntegrationConfig {

    @Bean
    MemberService getMemberService(MemberRepository memberRepository, ProfileService profileService){
        return new MemberServiceImpl(memberRepository, profileService);}

    @Bean
    BenefitService getBenefitService(BenefitRepository benefitRepository, ProfileService profileService){
        return new BenefitServiceImpl(benefitRepository, profileService);}

    @Bean
    ProgramService getProgramService(ProgramRepository programRepository, ProfileService profileService){
        return new ProgramServiceImpl(programRepository, profileService);}

    @Bean
    UnionService getUnionService(AboutRepository aboutRepository, FaqRepository faqRepository,
                                 FaqCategoryRepository faqCategoryRepository, PodcastRepository podcastRepository,
                                 FileRepository fileRepository, FolderRepository folderRepository,
                                 ProfileService profileService, ContractInfoRepository contractInfoRepository){
        return new UnionServiceImpl(aboutRepository, faqRepository, faqCategoryRepository, podcastRepository,
                fileRepository, folderRepository, profileService, contractInfoRepository);}

    @Bean
    PostService getPostService(PostRepository postRepository, ProfileService profileService,
                               TopicRepository topicRepository, TagsRepository tagsRepository, ForumService forumService,
                               DraftPostRepository draftPostRepository, PublishedPostsRepository publishedPostsRepository,
                               TrashedPostRepository trashedPostRepository, CloudinaryHelper cloudinaryHelper,
                               ImageRepository imageRepository){
        return new PostServiceImpl(postRepository, profileService, topicRepository, tagsRepository, forumService,

                draftPostRepository, publishedPostsRepository, trashedPostRepository, cloudinaryHelper, imageRepository);}

    @Bean
    TopicService getTopicService(TopicRepository topicRepository,
                                 ProfileService profileService,
                                 PostRepository postRepository, ForumService forumService){
        return new TopicServiceImpl(topicRepository, profileService, postRepository, forumService);}

    @Bean
    ImageService getImageService(ImageRepository imageRepository){
        return new ImageServiceImpl(imageRepository);}

    @Bean
    DefaultTagService getTagService(DefaultTagsRepository defaultTagsRepository){
        return new DefaultTagServiceImpl(defaultTagsRepository);
    }

    @Bean
    CalendarService getCalendarService(RestTemplate restTemplate){
        return new HttpCalendarServiceImpl(restTemplate);
    }
}
