package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.User;
import com.ttcs.ttcs_app.model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, String> {
    List<UserActivityLog> findByUserOrderByCreatedAtAsc(User user);
    List<UserActivityLog> findAllByOrderByCreatedAtAsc();
    List<UserActivityLog> findTop5ByUserOrderByCreatedAtDesc(User user);
}