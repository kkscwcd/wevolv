package com.wevolv.unionservice.service.impl;

import com.wevolv.unionservice.exceptions.NotFoundException;
import com.wevolv.unionservice.integration.profile.service.ProfileService;
import com.wevolv.unionservice.model.Benefit;
import com.wevolv.unionservice.model.dto.BenefitDto;
import com.wevolv.unionservice.model.enums.UnionProvider;
import com.wevolv.unionservice.repository.BenefitRepository;
import com.wevolv.unionservice.service.BenefitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BenefitServiceImpl implements BenefitService {

    private final BenefitRepository benefitRepository;

    public BenefitServiceImpl(BenefitRepository benefitRepository, ProfileService profileService) {
        this.benefitRepository = benefitRepository;
    }

    @Override
    public Benefit createNewBenefit(BenefitDto benefitDto) {
        Benefit newBenefit = Benefit.builder()
                .id(UUID.randomUUID().toString())
                .title(benefitDto.getTitle())
                .description(benefitDto.getDescription())
                .isActive(benefitDto.isActive())
                .build();

        benefitRepository.save(newBenefit);
        return newBenefit;
    }

    @Override
    public void deleteBenefit(String benefitId) {
        benefitRepository.deleteById(benefitId);
    }

    @Override
    public Benefit updateBenefit(BenefitDto benefitDto, String benefitId) {
        var benefit = benefitRepository.findById(benefitId)
                .orElseThrow(() -> new NotFoundException(String.format("Benefit with benefitId %s doesn't exist", benefitId)));
        benefit.setTitle(benefitDto.getTitle());
        benefit.setDescription(benefitDto.getDescription());
        benefit.setActive(benefitDto.isActive());
        benefitRepository.save(benefit);

        return benefit;
    }

    @Override
    public Map<String, Object> getAllBenefits(PageRequest page) {
        var benefits = benefitRepository.findAll(page);
        return populateBenefitMapResponse(benefits);
    }

    private Map<String, Object> populateBenefitMapResponse(Page<Benefit> benefits) {
        Map<String, Object> response = new HashMap<>();
        response.put("benefits", benefits.getContent());
        response.put("currentPage", benefits.getNumber());
        response.put("totalItems", benefits.getTotalElements());
        response.put("totalPages", benefits.getTotalPages());
        response.put("hasPrevious", benefits.hasPrevious());
        response.put("hasNext", benefits.hasNext());
        return response;
    }

    @Override
    public Benefit getBenefitById(String benefitId) {
        return benefitRepository.findById(benefitId)
                .orElseThrow(() -> new NotFoundException(String.format("Benefit with benefitId %s doesn't exist", benefitId)));
    }

    @Override
    public Benefit setBenefitActive(String benefitId) {
        var existingBenefit = getBenefitById(benefitId);
        existingBenefit.setActive(true);
        return existingBenefit;
    }

    @Override
    public Benefit setBenefitAsNonActive(String benefitId) {
        var existingBenefit = getBenefitById(benefitId);
        existingBenefit.setActive(false);
        return existingBenefit;
    }

    @Override
    public List<Benefit> searchBenefitByTitle(String title) {
        return benefitRepository.findByTitle(title);
    }
}
