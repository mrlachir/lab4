package com.example.model.tableperclass;
import javax.persistence.*;

@Entity
public class Electronique extends Produit {
    private int garantie;
    public Electronique() {}
    public Electronique(String nom, int garantie) { super(nom); this.garantie = garantie; }
    public int getGarantie() { return garantie; }
    public void setGarantie(int garantie) { this.garantie = garantie; }
}
