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

    @Query(value = """
  select * from users u
  where (:q = '' or u.nombre LIKE concat('%', :q, '%')
                 or u.email  LIKE concat('%', :q, '%')
                 or u.dpi    LIKE  concat('%', :q, '%'))
    and (:rol is null or u.rol = :rol)
    and (:enabled is null or u.enabled = :enabled)
  order by u.created_at desc
  """,
            countQuery = """
  select count(*) from users u
  where (:q = '' or u.nombre LIKE concat('%', :q, '%')
                 or u.email  LIKE concat('%', :q, '%')
                 or u.dpi    LIKE  concat('%', :q, '%'))
    and (:rol is null or u.rol = :rol)
    and (:enabled is null or u.enabled = :enabled)
  """,
            nativeQuery = true)
    Page<UsuarioDbEntity> search(@Param("q") String q,
            @Param("rol") String rol,
            @Param("enabled") Boolean enabled,
            Pageable pageable);
}
