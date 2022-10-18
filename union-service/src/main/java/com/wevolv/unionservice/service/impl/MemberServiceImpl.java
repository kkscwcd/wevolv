package com.wevolv.unionservice.service.impl;

import com.wevolv.unionservice.exceptions.NotFoundException;
import com.wevolv.unionservice.integration.profile.service.ProfileService;
import com.wevolv.unionservice.model.Member;
import com.wevolv.unionservice.model.dto.MemberDto;
import com.wevolv.unionservice.repository.MemberRepository;
import com.wevolv.unionservice.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ProfileService profileService;
    public MemberServiceImpl(MemberRepository memberRepository, ProfileService profileService) {
        this.memberRepository = memberRepository;
        this.profileService = profileService;
    }

    @Override
    public Member createNewMember(MemberDto memberDto) {

        Member newMember = Member.builder()
                .id(UUID.randomUUID().toString())
                .firstName(memberDto.getFirstName())
                .lastName(memberDto.getLastName())
                .isPaid(memberDto.getIsPaid())
                .membershipStart(Instant.now())
                .isPlayer(memberDto.getIsPlayer())
                .isRepresentative(memberDto.getIsRepresentative())
                .team(memberDto.getTeam())
                .build();

        memberRepository.save(newMember);
        return newMember;
    }

    @Override
    public void deleteMember(String memberId) {
        memberRepository.deleteById(memberId);
    }

    @Override
    public Member updateMember(MemberDto memberDto, String memberId) {
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(String.format("Member with memberId %s doesn't exist", memberId)));
        member.setFirstName(memberDto.getFirstName());
        member.setLastName(memberDto.getLastName());
        member.setIsPaid(memberDto.getIsPaid());
        member.setMembershipStart(Instant.now());
        member.setIsPlayer(memberDto.getIsPlayer());
        member.setIsRepresentative(memberDto.getIsRepresentative());
        member.setTeam(memberDto.getTeam());
        memberRepository.save(member);

        return member;
    }

    @Override
    public Map<String, Object> getAllMembers(PageRequest page) {
        var members = memberRepository.findAll(page);
        return populateMemberMapResponse(members);
    }

    private Map<String, Object> populateMemberMapResponse(Page<Member> members) {
        Map<String, Object> response = new HashMap<>();
        response.put("members", members.getContent());
        response.put("currentPage", members.getNumber());
        response.put("totalItems", members.getTotalElements());
        response.put("totalPages", members.getTotalPages());
        response.put("hasPrevious", members.hasPrevious());
        response.put("hasNext", members.hasNext());
        return response;
    }

    @Override
    public Member getMemberById(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(String.format("Member with memberId %s doesn't exist", memberId)));
    }

    @Override
    public Map<String, Object> search(String word, PageRequest page) {
        var members = memberRepository.getMemberByFirstNameOrLastName(word, page);
        return populateMemberMapResponse(members);
    }

    @Override
    public Map<String, Object> getRepresentativeMembers(PageRequest paging) {
        var members = memberRepository.getMemberByIsRepresentative(true, paging);
        return populateMemberMapResponse(members);
    }

    @Override
    public Map<String, Object> getPlayerMembers(PageRequest paging) {
        var members = memberRepository.getMemberByIsPlayer(true, paging);
        return populateMemberMapResponse(members);
    }


}
