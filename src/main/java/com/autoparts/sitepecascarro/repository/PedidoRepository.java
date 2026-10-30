package com.autoparts.sitepecascarro.repository;

import com.autoparts.sitepecascarro.entity.Pedido;
import com.autoparts.sitepecascarro.entity.StatusPedido;
import com.autoparts.sitepecascarro.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findAllByOrderByDataDesc();
    long countByStatus(StatusPedido status);
    long countByCliente(Usuario cliente);
}
