package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.Member;
import com.wevolv.unionservice.model.MemberInvite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MemberInviteRepository extends MongoRepository<MemberInvite, String> {

   
}

