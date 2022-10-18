package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.ContractInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ContractInfoRepository extends MongoRepository<ContractInfo, String> {

    Optional<List<ContractInfo>> findAllByKeycloakId(String keycloakId);
}
