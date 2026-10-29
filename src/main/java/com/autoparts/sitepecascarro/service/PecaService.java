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

    // Catálogo: cada filtro vazio deixa todas as peças passarem
    public List<Peca> buscar(String texto, String categoria, String marca, Double precoMax) {
        return pecaRepository.findAll().stream()
                .filter(p -> texto == null || texto.isBlank()
                        || (p.getNome() != null && p.getNome().toLowerCase().contains(texto.trim().toLowerCase())))
                .filter(p -> categoria == null || categoria.isBlank() || categoria.equals(p.getCategoria()))
                .filter(p -> marca == null || marca.isBlank() || marca.equals(p.getMarca()))
                .filter(p -> precoMax == null || (p.getPreco() != null && p.getPreco() <= precoMax))
                .toList();
    }

    // Opções do filtro de marca
    public List<String> listarMarcas() {
        return pecaRepository.findAll().stream()
                .map(Peca::getMarca)
                .filter(m -> m != null && !m.isBlank())
                .distinct().sorted().toList();
    }

    // Opções do filtro de categoria
    public List<String> listarCategorias() {
        return pecaRepository.findAll().stream()
                .map(Peca::getCategoria)
                .filter(c -> c != null && !c.isBlank())
                .distinct().sorted().toList();
    }

    // Criar, editar e excluir entram aqui na etapa do CRUD completo.
    public Peca salvar(Peca peca){
        // salva peca no repositorio
        // criar ou editar
        return pecaRepository.save(peca);
    }

    public Peca buscarPorId(Long id){
        //buscar a peca
        return pecaRepository.findById(id).orElse(null);
    }

    public void excluir(Long id){
        //excluir peca
        pecaRepository.deleteById(id);
    }
}
