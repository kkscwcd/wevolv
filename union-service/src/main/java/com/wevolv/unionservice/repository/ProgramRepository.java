package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.Program;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProgramRepository extends MongoRepository<Program, String> {
}
