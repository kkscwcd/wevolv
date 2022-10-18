package com.wevolv.unionservice.service;

import com.wevolv.unionservice.model.Member;
import com.wevolv.unionservice.model.dto.MemberDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

public interface MemberService {
    Member createNewMember(MemberDto memberDto);

    void deleteMember(String memberId);

    Member updateMember(MemberDto memberDto, String memberId);

    Map<String, Object> getAllMembers(PageRequest page);

    Member getMemberById(String memberId);

    Map<String, Object> search(String word, PageRequest page);

    Map<String, Object> getRepresentativeMembers(PageRequest paging);

    Map<String, Object> getPlayerMembers(PageRequest paging);
}
