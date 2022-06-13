package com.dbhstudios.akdmvm.domain.respository;

import com.dbhstudios.akdmvm.domain.entity.model.Agrupacion;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgrupacionJpaRepository extends PagingAndSortingRepository<Agrupacion, Long>{


	@Override
	List<Agrupacion> findAll();
	List<Agrupacion> findByTextoContainingIgnoreCase(String cadena);

	List<Agrupacion> findAllByOrderByTexto();


}
