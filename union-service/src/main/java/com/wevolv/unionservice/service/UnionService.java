package com.wevolv.unionservice.service;

import com.wevolv.unionservice.model.*;
import com.wevolv.unionservice.model.dto.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UnionService {

    About createAbout(AboutDto aboutDto);

    void deleteAbout(String aboutId);

    About updateAbout(AboutDto aboutDto, String aboutId);

    Faq createFaq(FaqDto faqDto);

    void deleteFaq(String faqId);

    Faq updateFaq(FaqDto faqDto, String faqId);

    Faq getById(String faqId);

    FaqCategory createFaqCategory(FaqCategoryDto faqDto);

    FaqCategory updateFaqCategory(FaqCategoryDto faqDto, String categoryId);

    void deleteFaqCategory(String categoryId);

    Map<String, Object> allFaqForCategory(String categoryId, PageRequest paging);

    About getAboutByUnionProvider(String unionProvider);

    Podcast addPodcast(String keycloakId, PodcastDto highlightDto);

    Podcast updatePodcast(String keycloakId, PodcastDto podcastDto, String podcastId);

    void deletePodcast(String keycloakId, String podcastId);

    File upload(MultipartFile multipartFile, String keycloakId,String userName) throws IOException;

    Folder contractReviewInfo(ContractReviewInfoDto contractInfoDto, String keycloakId);

    Podcast getPodcast(String keycloakId, String podcastId);

    Map<String, Object> getAllPodcast(String keycloakId, PageRequest page);

    Folder getUserFolder(String folderId);

    Map<String, Object> getAllFolders(PageRequest paging);

    List<File> getAllFolderFiles(String folderId);
}
