package com.crm.group7.repositories;

import com.crm.group7.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID>, JpaSpecificationExecutor<Cliente> {

    @Query("SELECT c FROM Cliente c " +
            "JOIN c.indirizzi i " +
            "JOIN i.comune co " +
            "JOIN co.provincia p " +
            "WHERE i.tipoIndirizzo = com.crm.group7.entities.enums.TipoIndirizzo.SEDE_LEGALE")
    Page<Cliente> findAllSortedByProvinciaSedeLegale(Pageable pageable);

    boolean existsByPartitaIva(String partitaIva);

    boolean existsByPec(String pec);

    boolean existsByEmailContatto(String emailContatto);
    
    boolean existsByPartitaIvaAndIdClienteNot(String partitaIva, UUID idCliente);

    boolean existsByPecAndIdClienteNot(String pec, UUID idCliente);

    boolean existsByEmailContattoAndIdClienteNot(String emailContatto, UUID idCliente);
}
