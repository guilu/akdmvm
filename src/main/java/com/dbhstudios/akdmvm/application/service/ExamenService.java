package com.dbhstudios.akdmvm.application.service;


import com.dbhstudios.akdmvm.domain.entity.model.Examen;
import com.dbhstudios.akdmvm.domain.respository.ExamenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamenService {

    private final ExamenRepository examenRepository;

    @Autowired
    public ExamenService(ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    public List<Examen> getExamenes(){
        return this.examenRepository.findAll();
    }

    public Optional<Examen> getExamen(Long id){
        return this.examenRepository.findById(id);
    }

}
