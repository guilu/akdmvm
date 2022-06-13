package com.dbhstudios.akdmvm.domain.dto;


import com.dbhstudios.akdmvm.domain.entity.model.Test;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class TestDTO {

    private ArrayList<Long> preguntas;
    private boolean terminado;
    private int aciertos;
    private int fallos;

    public TestDTO(){};

    public TestDTO(Test test){
        this.aciertos = test.getAciertos();
        this.fallos = test.getFallos();
        this.terminado = test.isFinalizado();

    }
}
