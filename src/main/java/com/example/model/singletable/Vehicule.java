package com.example.model.singletable;
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicule_type", discriminatorType = DiscriminatorType.STRING)
@Table(indexes = @Index(name = "idx_vehicule_type", columnList = "vehicule_type"))
public abstract class Vehicule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String marque;
    public Vehicule() {}
    public Vehicule(String marque) { this.marque = marque; }
    public Long getId() { return id; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    @Override public String toString() { return getClass().getSimpleName() + ": " + marque; }
}
