package com.college.notification.repository;

import com.college.notification.entity.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QueryRepository extends JpaRepository<Query, Long> {

    // Load only unresolved queries
    List<Query> findByResolvedFalse();

    // Load only resolved queries
    List<Query> findByResolvedTrue();
}
