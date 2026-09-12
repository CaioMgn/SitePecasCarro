package com.autoparts.sitepecascarro.service;

import com.autoparts.sitepecascarro.entity.Peca;
import com.autoparts.sitepecascarro.repository.PecaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

//quando surgir uma regra (filtrar, validar, ordenar), ela entra aqui dentro.
@Service
public class PecaService {

    private final PecaRepository pecaRepository;

    public PecaService(PecaRepository pecaRepository) {
        this.pecaRepository = pecaRepository;
    }

    public List<Peca> listarTodas() {
        return pecaRepository.findAll();
    }

    // Criar, editar e excluir entram aqui na etapa do CRUD completo.
}
