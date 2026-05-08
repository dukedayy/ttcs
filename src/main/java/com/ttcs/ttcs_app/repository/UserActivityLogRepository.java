package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, String> {
}