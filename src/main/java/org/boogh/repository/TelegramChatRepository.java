package org.boogh.repository;

import org.boogh.domain.TelegramChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the TelegramChat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TelegramChatRepository extends JpaRepository<TelegramChat, Long> {

    @Query("select telegramChat from TelegramChat telegramChat where telegramChat.user.id = ?1")
    List<TelegramChat> findTelegramChatByUserId(long id);

    Optional<TelegramChat> findTelegramChatByTelegramUserId(Long telegramUserId);
}
