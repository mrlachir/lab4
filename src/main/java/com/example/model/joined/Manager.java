package com.example.model.joined;
import javax.persistence.*;

@Entity
public class Manager extends Employe {
    private int nombreEquipes;
    public Manager() {}
    public Manager(String nom, int nombreEquipes) { super(nom); this.nombreEquipes = nombreEquipes; }
    public int getNombreEquipes() { return nombreEquipes; }
    public void setNombreEquipes(int nombreEquipes) { this.nombreEquipes = nombreEquipes; }
}
