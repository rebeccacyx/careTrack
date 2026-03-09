package com.careapp.repository;

import com.careapp.domain.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends MongoRepository<Organization, String> {
    // Find organization by name
    Organization findByName(String name);
}
