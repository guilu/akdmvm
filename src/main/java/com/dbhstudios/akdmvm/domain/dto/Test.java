package com.dbhstudios.akdmvm.domain.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Test {

    private ArrayList<Long> preguntas;
    private boolean terminado;
    private int aciertos;
    private int fallos;
}
