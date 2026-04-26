package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, String> {
}
