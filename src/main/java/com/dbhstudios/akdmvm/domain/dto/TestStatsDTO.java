package com.dbhstudios.akdmvm.domain.dto;


import lombok.Getter;
import lombok.Setter;

public class TestStatsDTO {

    public int testFinalizados;
    public double mediaAciertos;
    public double mediaFallos;

    public TestStatsDTO(){
        this.testFinalizados = 0;
        this.mediaAciertos = 0f;
        this.mediaFallos = 0f;
    }


    public int getTestFinalizados() {
        return testFinalizados;
    }

    public void setTestFinalizados(int testFinalizados) {
        this.testFinalizados = testFinalizados;
    }

    public double getMediaAciertos() {
        return mediaAciertos;
    }

    public void setMediaAciertos(double mediaAciertos) {
        this.mediaAciertos = mediaAciertos;
    }

    public double getMediaFallos() {
        return mediaFallos;
    }

    public void setMediaFallos(double mediaFallos) {
        this.mediaFallos = mediaFallos;
    }

    public String toString(){
        return "TestStatsDTO TF:"+this.testFinalizados+",MA:"+this.mediaAciertos+",MF:"+this.mediaFallos;
    }
}
