package com.dbhstudios.akdmvm.domain.entity.model;


import com.dbhstudios.akdmvm.auth.domain.model.User;
import com.dbhstudios.akdmvm.domain.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "Test")
@EntityListeners(AuditingEntityListener.class)
@Table(schema = "BDD_AKDMVM", name = "TB06_TEST")
public class Test extends BaseEntity {

    private boolean finalizado;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PreguntaTest> preguntas;

    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    @JsonIgnore
    private User user;

    private int aciertos;
    private int fallos;

    private int numeroPreguntasXTema;
    private int numeroPreguntasTotal;

    public Test(){
    }

    public Test(int numeroPreguntasXTema, int numeroPreguntasTotal){
        this.aciertos=0;
        this.fallos=0;
        this.numeroPreguntasXTema = numeroPreguntasXTema;
        this.numeroPreguntasTotal = numeroPreguntasTotal;
    }

    public void setPreguntasTest(List<Pregunta> preguntas){
        if (preguntas == null ){
            return;
        }
        List<PreguntaTest> preguntasTest = new ArrayList<>();
        for (Pregunta pregunta: preguntas) {
            PreguntaTest preguntaTest = new PreguntaTest(pregunta);
            preguntaTest.setTest(this);
            preguntasTest.add(preguntaTest);

        }
        this.setPreguntas(preguntasTest);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String toString(){
        return "A:"+this.getAciertos()+" F:"+this.getFallos()+" PxT:"+this.getNumeroPreguntasXTema()+" PT:"+this.getNumeroPreguntasTotal();
    }
}
