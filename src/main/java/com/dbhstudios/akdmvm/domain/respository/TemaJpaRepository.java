package com.dbhstudios.akdmvm.domain.respository;

import com.dbhstudios.akdmvm.domain.entity.DomainModelNames;
import com.dbhstudios.akdmvm.domain.entity.model.Agrupacion;
import com.dbhstudios.akdmvm.domain.entity.model.Tema;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemaJpaRepository extends PagingAndSortingRepository<Tema, Long>{
	
	@Override
	List<Tema> findAll();

	List<Tema> findByAgrupacion(Agrupacion agrupacion);

	@Query(value = "SELECT DISTINCT T.* FROM "+DomainModelNames.SCHEMA+"."+DomainModelNames.TB03_TEMA +" T, " + DomainModelNames.SCHEMA + "." + DomainModelNames.TB04_PREGUNTA  + " P WHERE T.ID = P.TEMA_ID",nativeQuery = true)
	List<Tema> findTemasConPreguntas();

	List<Tema> findByTextoContainingIgnoreCase(String cadena);

	List<Tema> findAllByOrderByTextoAsc();
}
