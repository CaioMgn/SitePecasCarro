package com.autoparts.sitepecascarro.repository;

import com.autoparts.sitepecascarro.entity.Papel;
import com.autoparts.sitepecascarro.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.autoparts.sitepecascarro.entity.Papel;
import java.util.List;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByPapel(Papel papel);

    List<Usuario> findByPapelOrderByNomeAsc(Papel papel);
}
