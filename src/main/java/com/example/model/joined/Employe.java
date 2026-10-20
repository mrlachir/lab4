package com.example.model.joined;
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Employe {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    public Employe() {}
    public Employe(String nom) { this.nom = nom; }
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    @Override public String toString() { return getClass().getSimpleName() + ": " + nom; }
}
