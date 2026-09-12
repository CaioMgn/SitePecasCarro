package com.autoparts.sitepecascarro.repository;

import com.autoparts.sitepecascarro.entity.Peca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//é quem conversa com o banco. É onde ficam as operações de salvar, buscar, listar e deletar. 
// Ele fica entre o Service e o banco de dados.

@Repository
public interface PecaRepository extends JpaRepository<Peca, Long> {
    //Só com extends JpaRepository, você já herda métodos prontos (save, lista todas, busca e deleta).
}
