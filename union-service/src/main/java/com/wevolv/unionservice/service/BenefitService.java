package com.wevolv.unionservice.service;

import com.wevolv.unionservice.model.Benefit;
import com.wevolv.unionservice.model.dto.BenefitDto;
import com.wevolv.unionservice.model.enums.UnionProvider;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

public interface BenefitService {
    Benefit createNewBenefit(BenefitDto benefitDto);

    void deleteBenefit(String benefitId);

    Benefit updateBenefit(BenefitDto benefitDto, String benefitId);

    Map<String, Object> getAllBenefits(PageRequest page);

    Benefit getBenefitById(String benefitId);

    Benefit setBenefitActive(String benefitId);

    Benefit setBenefitAsNonActive(String benefitId);

    List<Benefit> searchBenefitByTitle(String name);
}
