// user/infrastructure/outputadapters/persistence/repository/UsuarioJpaRepository.java
package com.user.microservice.user.infrastructure.outputadapters.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.user.microservice.user.infrastructure.outputadapters.persistence.entity.UsuarioDbEntity;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioDbEntity, UUID> {

    Optional<UsuarioDbEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByDpi(String dpi);

    @Query(
            value = """
    SELECT *
    FROM users u
    WHERE (
            (:q IS NULL OR :q = '')
            OR LOWER(u.nombre) LIKE CONCAT('%', LOWER(:q), '%')
            OR LOWER(u.email)  LIKE CONCAT('%', LOWER(:q), '%')
            OR u.dpi           LIKE CONCAT('%', :q, '%')
          )
      AND ( :rol IS NULL OR :rol = '' OR u.rol = :rol )
      AND ( :enabled IS NULL OR u.enabled = :enabled )
    ORDER BY u.created_at DESC
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM users u
    WHERE (
            (:q IS NULL OR :q = '')
            OR LOWER(u.nombre) LIKE CONCAT('%', LOWER(:q), '%')
            OR LOWER(u.email)  LIKE CONCAT('%', LOWER(:q), '%')
            OR u.dpi           LIKE CONCAT('%', :q, '%')
          )
      AND ( :rol IS NULL OR :rol = '' OR u.rol = :rol )
      AND ( :enabled IS NULL OR u.enabled = :enabled )
    """,
            nativeQuery = true
    )
    Page<UsuarioDbEntity> search(
            @Param("q") String q,
            @Param("rol") String rol,
            @Param("enabled") Boolean enabled,
            Pageable pageable
    );
}
