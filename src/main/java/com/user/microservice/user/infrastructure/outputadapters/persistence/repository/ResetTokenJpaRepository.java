// user/infrastructure/outputadapters/persistence/repository/ResetTokenJpaRepository.java
package com.user.microservice.user.infrastructure.outputadapters.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.microservice.user.infrastructure.outputadapters.persistence.entity.ResetTokenDbEntity;

public interface ResetTokenJpaRepository extends JpaRepository<ResetTokenDbEntity, UUID> {
}
