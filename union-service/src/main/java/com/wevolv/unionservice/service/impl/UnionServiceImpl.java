package com.wevolv.unionservice.service.impl;

import com.wevolv.unionservice.exceptions.NotFoundException;
import com.wevolv.unionservice.integration.profile.service.ProfileService;
import com.wevolv.unionservice.model.*;
import com.wevolv.unionservice.model.dto.*;
import com.wevolv.unionservice.repository.*;
import com.wevolv.unionservice.service.UnionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
@Slf4j
public class UnionServiceImpl implements UnionService {

    private final AboutRepository aboutRepository;
    private final FaqRepository faqRepository;
    private final FaqCategoryRepository faqCategoryRepository;
    private final PodcastRepository podcastRepository;
    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final ProfileService profileService;
    private final ContractInfoRepository contractInfoRepository;

    public UnionServiceImpl(AboutRepository aboutRepository,
                            FaqRepository faqRepository,
                            FaqCategoryRepository faqCategoryRepository,
                            PodcastRepository podcastRepository, FileRepository fileRepository, FolderRepository folderRepository, ProfileService profileService, ContractInfoRepository contractInfoRepository) {
        this.aboutRepository = aboutRepository;
        this.faqRepository = faqRepository;
        this.faqCategoryRepository = faqCategoryRepository;

        this.podcastRepository = podcastRepository;
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.profileService = profileService;
        this.contractInfoRepository = contractInfoRepository;
    }

    @Override
    public About createAbout(AboutDto aboutDto) {
        About newAbout = About.builder()
                .id(UUID.randomUUID().toString())
                .description(aboutDto.getDescription())

                .build();

        aboutRepository.save(newAbout);
        return newAbout;
    }

    @Override
    public void deleteAbout(String aboutId) {aboutRepository.deleteById(aboutId);}

    @Override
    public About updateAbout(AboutDto aboutDto, String aboutId) {
        var about = aboutRepository.findById(aboutId)
                .orElseThrow(() -> new NotFoundException(String.format("About with aboutId %s doesn't exist", aboutId)));
        about.setDescription(aboutDto.getDescription());

        aboutRepository.save(about);

        return about;
    }

    @Override
    public Faq createFaq(FaqDto faqDto) {
        Faq.requestValidator(faqDto);
        var faq = new Faq(UUID.randomUUID().toString(), faqDto.getCategoryId(), faqDto.getTitle(), faqDto.getDescription());
        faqRepository.save(faq);

        return faq;
    }

    @Override
    public void deleteFaq(String faqId) {
        faqRepository.deleteById(faqId);
    }

    private Faq getFaq(String faqId) {
        return faqRepository.findById(faqId)
                .orElseThrow(() -> new NotFoundException(String.format("Faq %s does not exist", faqId)));
    }

    private FaqCategory getCategory(String categoryId) {
        return faqCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category %s does not exist", categoryId)));
    }

    @Override
    public Faq updateFaq(FaqDto faqDto, String faqId) {
        var faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new NotFoundException(String.format("Faq with faqId %s doesn't exist", faqId)));

        faq.setTitle(faqDto.getTitle());
        faq.setDescription(faqDto.getDescription());

        faqRepository.save(faq);

        return faq;
    }

    @Override
    public Faq getById(String faqId) {
        return faqRepository.findById(faqId)
                .orElseThrow(() -> new NotFoundException("Faq not found"));
    }

    @Override
    public FaqCategory createFaqCategory(FaqCategoryDto faqDto) {
        return faqCategoryRepository.save(new FaqCategory(UUID.randomUUID().toString(), faqDto.getName()));
    }

    @Override
    public FaqCategory updateFaqCategory(FaqCategoryDto faqDto, String categoryId) {
        var category = getCategory(categoryId);
        category.setName(faqDto.getName());
        return faqCategoryRepository.save(category);
    }

    @Override
    public void deleteFaqCategory(String categoryId) {
        var allCategoryFaq = faqRepository.findByCategoryId(categoryId);
        faqRepository.deleteAll(allCategoryFaq);
        faqCategoryRepository.deleteById(categoryId);
    }

    @Override
    public Map<String, Object> allFaqForCategory(String categoryId, PageRequest paging) {
        var allCategoryFaq = faqRepository.findByCategoryId(categoryId, paging);
        return populateCategoryMapResponse(allCategoryFaq);
    }

    @Override
    public About getAboutByUnionProvider(String unionProvider) {
        return aboutRepository.findByUnionProvider(unionProvider)
                .orElseThrow(() -> new NotFoundException(String.format("About %s not found for union provider", unionProvider)));
    }

    @Override
    public Podcast addPodcast(String keycloakId, PodcastDto podcastDto) {
        return podcastRepository.save(new Podcast(UUID.randomUUID().toString(),
                keycloakId, podcastDto.getTitle(),
                podcastDto.getDescription(),
                podcastDto.getLink()));
    }

    @Override
    public Podcast updatePodcast(String keycloakId, PodcastDto podcastDto, String podcastId) {
        var podcast = podcastRepository.findById(podcastId)
                .orElseThrow(() -> new NotFoundException(String.format("Podcast %s not found", podcastId)));
        podcast.setTitle(podcastDto.getTitle());
        podcast.setDescription(podcastDto.getDescription());
        podcast.setLink(podcastDto.getLink());
        return podcastRepository.save(podcast);
    }

    @Override
    public void deletePodcast(String keycloakId, String podcastId) {
        podcastRepository.deleteById(podcastId);
    }

    @Override
    public File upload(MultipartFile multipartFile, String keycloakId,String userName) throws IOException {
       /* File file = File.builder().build();
        try {
            // Clean the file name
             String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
              fileName = fileName.replace(" ", "");
              // Save file in fileSystem
              this.saveFileInFileSystem(multipartFile, fileName, keycloakId);

            file.setId(UUID.randomUUID().toString());
            file.setKeycloakId(keycloakId);
            file.setCreatedTime(Instant.now());
            file.setFileName(fileName);
            file.setFileType(multipartFile.getContentType());
            file.setFileSize(multipartFile.getSize());
            file.setFile(multipartFile.getBytes());
            fileRepository.save(file);

        } catch (Exception e) {
            throw new IOException("Error while saving PDF to database.");
        }*/

        File file = File.builder().build();
        try {
            file.setId(UUID.randomUUID().toString());
            file.setKeycloakId(keycloakId);
            file.setCreatedTime(Instant.now());
            file.setFileName(multipartFile.getName());
            file.setFileType(multipartFile.getContentType());
            file.setFileSize(multipartFile.getSize());
            file.setFile(multipartFile.getBytes());
            fileRepository.save(file);

        } catch (Exception e) {
            throw new IOException("Error while saving PDF to database.");
        }

        return file;
    }

    @Override
    public Folder contractReviewInfo(ContractReviewInfoDto contractInfoDto, String keycloakId) {
        //adding file to folder. Folder is created first time file is uploaded
        // each other time we ask does folder with keycloakId exists if yes add to existing folder do not again
        // create new.
        var existingContractInfo = contractInfoRepository.findAllByKeycloakId(keycloakId);
        existingContractInfo.ifPresent(
                ci -> log.info("This user has requested contract review {} times already", (long) ci.size()));

        var psi = profileService.userShortProfileByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User does not exist"));
        var file = fileRepository.findById(contractInfoDto.getFileId())
                .orElseThrow(() -> new NotFoundException(String.format("File %s does not exist", contractInfoDto.getFileId())));
        var folder = folderRepository.findByKeycloakId(keycloakId);
        if(folder.isPresent()){
            file.setFolderId(folder.get().getId());
            fileRepository.save(file);
            contractInfoRepository.save(new ContractInfo(UUID.randomUUID().toString(), keycloakId, contractInfoDto.getContractType()));
            return folder.get();
        } else {
            var newFolder = folderRepository.save(new Folder(UUID.randomUUID().toString(),
                    keycloakId, psi.getFirstName() + psi.getLastName()));
            file.setFolderId(newFolder.getId());
            fileRepository.save(file);
            contractInfoRepository.save(new ContractInfo(UUID.randomUUID().toString(), keycloakId, contractInfoDto.getContractType()));
            return newFolder;
        }
    }

    @Override
    public Podcast getPodcast(String keycloakId, String podcastId) {
        return podcastRepository.findById(podcastId)
                .orElseThrow(() -> new NotFoundException(String.format("Podcast %s not found", podcastId)));
    }

    @Override
    public Map<String, Object> getAllPodcast(String keycloakId, PageRequest page) {
        var podcast = podcastRepository.findAll(page);
        return populatePodcastMapResponse(podcast);
    }

    @Override
    public Folder getUserFolder(String folderId) {
        return folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundException(String.format("Folder %s not found", folderId)));
    }

    @Override
    public Map<String, Object> getAllFolders(PageRequest page) {
        var folders = folderRepository.findAll(page);
        return populateFoldersMapResponse(folders);
    }

    @Override
    public List<File> getAllFolderFiles(String folderId) {
        return fileRepository.findAllByFolderId(folderId);
    }

    private Map<String, Object> populateFoldersMapResponse(Page<Folder> folders) {
        Map<String, Object> response = new HashMap<>();
        response.put("folders", folders.getContent());
        response.put("currentPage", folders.getNumber());
        response.put("totalItems", folders.getTotalElements());
        response.put("totalPages", folders.getTotalPages());
        response.put("hasPrevious", folders.hasPrevious());
        response.put("hasNext", folders.hasNext());
        return response;
    }

    private Map<String, Object> populatePodcastMapResponse(Page<Podcast> podcast) {
        Map<String, Object> response = new HashMap<>();
        response.put("podcast", podcast.getContent());
        response.put("currentPage", podcast.getNumber());
        response.put("totalItems", podcast.getTotalElements());
        response.put("totalPages", podcast.getTotalPages());
        response.put("hasPrevious", podcast.hasPrevious());
        response.put("hasNext", podcast.hasNext());
        return response;
    }

    private Map<String, Object> populateCategoryMapResponse(Page<Faq> faq) {
        Map<String, Object> response = new HashMap<>();
        response.put("faq", faq.getContent());
        response.put("currentPage", faq.getNumber());
        response.put("totalItems", faq.getTotalElements());
        response.put("totalPages", faq.getTotalPages());
        response.put("hasPrevious", faq.hasPrevious());
        response.put("hasNext", faq.hasNext());
        return response;
    }

    /*private void saveFileInFileSystem(MultipartFile file,String fileName,String keycloakId)throws IOException{
        //java.io.File dirs = new java.io.File(fileUploadRootDirectory,keycloakId);
       // If directory not exist, Create it
        if (!dirs.exists()) {
            dirs.mkdirs();
        }
        // Copy file to the target location (Replacing existing file with the same name)
        Path targetLocation = Paths.get(dirs.getPath(), fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }*/
}
