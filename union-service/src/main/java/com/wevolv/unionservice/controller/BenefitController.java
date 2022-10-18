package com.wevolv.unionservice.controller;

import com.wevolv.unionservice.keycloak.AuthorizationMapper;
import com.wevolv.unionservice.keycloak.KeycloakUtils;
import com.wevolv.unionservice.model.Benefit;
import com.wevolv.unionservice.model.dto.ActivateBenefitResponse;
import com.wevolv.unionservice.model.dto.BenefitDto;
import com.wevolv.unionservice.model.dto.ObjectCreatedResponse;
import com.wevolv.unionservice.model.dto.ObjectDeletedResponse;
import com.wevolv.unionservice.model.enums.UnionProvider;
import com.wevolv.unionservice.service.BenefitService;
import com.wevolv.unionservice.util.TokenDecoder;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import static com.wevolv.unionservice.util.TokenDecoder.getUserIdFromToken;

@Slf4j
@RestController
@RequestMapping("/benefit")
public class BenefitController {

    @Autowired
    AuthorizationMapper authorizationMapper;

    private final BenefitService benefitService;

    public BenefitController(BenefitService benefitService) {
        this.benefitService = benefitService;
    }

    @PostMapping(value = "/create")
    public ObjectCreatedResponse createNewBenefit(final KeycloakAuthenticationToken token, @RequestBody BenefitDto benefitDto) {
    /*    log.info("Groups {}", KeycloakUtils.groupsFrom(token));
        log.info("Authorization: {}", KeycloakUtils.groupAuthorizationFrom(token));*/

        /*UnionProvider unionProvider = null;
*/
        //Get user subscribed group from keycloak token
     /*   final var groups = KeycloakUtils.groupsFrom(token);
        var union = authorizationMapper.extractUnion(groups);*/
        /*if(union.isPresent()){
            unionProvider = UnionProvider.valueOf(union.get().getName());
        }*/

        var benefit = benefitService.createNewBenefit(benefitDto);
        return new ObjectCreatedResponse(benefit.getId());
    }

    @DeleteMapping(value = "/delete/{benefitId}")
    public ObjectDeletedResponse deleteBenefit(HttpServletRequest request, @PathVariable String benefitId){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        benefitService.deleteBenefit(benefitId);
        return new ObjectDeletedResponse(true);
    }

    @PostMapping(value = "/update/{benefitId}")
    public Benefit updateBenefit(HttpServletRequest request, @RequestBody BenefitDto benefitDto, @PathVariable String benefitId){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        return benefitService.updateBenefit(benefitDto, benefitId);
    }

    @GetMapping(value = "/all")
    public Map<String, Object> getAllBenefits(HttpServletRequest request,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "3") int size){

        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        return benefitService.getAllBenefits(paging);
    }

    @GetMapping(value = "/{benefitId}")
    public Benefit getBenefitById(HttpServletRequest request, @PathVariable String benefitId){

        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        return benefitService.getBenefitById(benefitId);
    }

    @PostMapping(value = "/active/{benefitId}")
    public ActivateBenefitResponse setBenefitActive(HttpServletRequest request, @PathVariable String benefitId){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        benefitService.setBenefitActive(benefitId);
        return new ActivateBenefitResponse(true);
    }

    @PostMapping(value = "/non/active/{benefitId}")
    public ActivateBenefitResponse setBenefitAsNonActive(HttpServletRequest request, @PathVariable String benefitId){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        benefitService.setBenefitAsNonActive(benefitId);
        return new ActivateBenefitResponse(false);
    }

    @GetMapping(value = "/search/{title}")
    public List<Benefit> searchBenefitByName(@PathVariable String title){
       return benefitService.searchBenefitByTitle(title);
    }

}
