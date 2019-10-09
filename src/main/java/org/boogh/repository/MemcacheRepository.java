package org.boogh.repository;

import org.boogh.domain.Memcache;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Memcache entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MemcacheRepository extends JpaRepository<Memcache, Long> {

    Optional<Memcache> findMemcacheByTelegramId(Long telegramId);

    Optional<Memcache> findMemcacheByHash(String hash);
}
