package jsl.moum.moum.lifecycle.domain.repository;

import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LifecycleRepository extends JpaRepository<LifecycleEntity, Integer> {
}
