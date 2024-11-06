package jsl.moum.chatappmodule.auth.domain.repository;

import jsl.moum.chatappmodule.auth.domain.entity.RefreshEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends R2dbcRepository<RefreshEntity, Long> {

    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);
}