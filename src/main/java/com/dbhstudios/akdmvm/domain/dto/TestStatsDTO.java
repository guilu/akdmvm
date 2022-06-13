package com.dbhstudios.akdmvm.domain.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestStatsDTO {

    public int testFinalizados;
    public double mediaAciertos;
    public double mediaFallos;

    public TestStatsDTO(){
        this.testFinalizados = 0;
        this.mediaAciertos = 0f;
        this.mediaFallos = 0f;
    }

    public String toString(){
        return "TestStatsDTO TF:"+this.testFinalizados+",MA:"+this.mediaAciertos+",MF:"+this.mediaFallos;
    }
}
