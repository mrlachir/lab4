package com.example.model.singletable;
import javax.persistence.*;

@Entity
@DiscriminatorValue("MOTO")
public class Moto extends Vehicule {
    private boolean sidecar;
    public Moto() {}
    public Moto(String marque, boolean sidecar) { super(marque); this.sidecar = sidecar; }
    public boolean isSidecar() { return sidecar; }
    public void setSidecar(boolean sidecar) { this.sidecar = sidecar; }
}
