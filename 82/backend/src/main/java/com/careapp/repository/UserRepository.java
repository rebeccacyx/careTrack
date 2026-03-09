package com.careapp.repository;

import com.careapp.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Query methods based on username (backward compatibility)
    User findByUname(String uname);
    User findByUnameAndPassword(String uname, String password);
    
    // Query methods based on email (frontend usage)
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);

    // Password reset
    User findByPasswordResetToken(String passwordResetToken);
    
    // Find users by organization ID
    List<User> findByOrganizationId(String organizationId);
    
    // Find users by organization ID and user type
    List<User> findByOrganizationIdAndUserType(String organizationId, String userType);
    
    // Find user by organization ID and user type (first match)
    User findFirstByOrganizationIdAndUserType(String organizationId, String userType);
}

