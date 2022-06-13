package com.dbhstudios.akdmvm.domain.respository;

import com.dbhstudios.akdmvm.domain.entity.model.Examen;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamenRepository extends PagingAndSortingRepository<Examen, Long> {

    @Override
    List<Examen> findAll();

}
