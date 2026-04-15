package com.enterprise.hr.repository;

import com.enterprise.hr.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndDeletedFalse(String email);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    Page<User> findAllByDeletedFalse(Pageable pageable);

    List<User> findAllByManagerIdAndDeletedFalse(Long managerId);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.role.id = :roleId")
    List<User> findAllByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdWithRole(@Param("id") Long id);
}
