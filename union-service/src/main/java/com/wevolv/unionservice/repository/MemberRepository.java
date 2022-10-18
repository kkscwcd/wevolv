package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.Member;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MemberRepository extends MongoRepository<Member, String> {

    @Query(value = "{" +
            "  '$expr': {" +
            "    '$regexMatch': {" +
            "      'input': { '$concat': ['$firstName', ' ', '$lastName'] }," +
            "      'regex': '?0'," +
            "      'options': 'i'" +
            "    }" +
            "  }" +
            "})")
    Page<Member> getMemberByFirstNameOrLastName(String word, PageRequest page);

    Page<Member> getMemberByIsRepresentative(Boolean b, PageRequest paging);

    Page<Member> getMemberByIsPlayer(boolean b, PageRequest paging);
}

