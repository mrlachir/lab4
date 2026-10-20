package com.example.model.joined;
import javax.persistence.*;

@Entity
public class Developpeur extends Employe {
    private String langage;
    public Developpeur() {}
    public Developpeur(String nom, String langage) { super(nom); this.langage = langage; }
    public String getLangage() { return langage; }
    public void setLangage(String langage) { this.langage = langage; }
}
