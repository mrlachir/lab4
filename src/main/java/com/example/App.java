package com.example;
import com.example.model.singletable.*;
import com.example.model.joined.*;
import com.example.model.tableperclass.*;
import javax.persistence.*;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-inheritance");
        try {
            EntityManager em = emf.createEntityManager();
            try {
                Voiture voiture = new Voiture("Toyota", 4);
                Moto moto = new Moto("Harley", true);
                Developpeur dev = new Developpeur("Alice", "Java");
                Manager manager = new Manager("Bob", 3);
                Livre livre = new Livre("Clean Code", "Robert C. Martin");
                Electronique electronique = new Electronique("Smartphone", 24);
                Transactions.executer(em, context -> {
                    context.persist(voiture); context.persist(moto);
                    context.persist(dev); context.persist(manager);
                    context.persist(livre); context.persist(electronique);
                });
                em.clear(); // Force reads from the database rather than the first-level cache.
                System.out.println("=== CREATION: 6 entites inserees ===");
                lister(em);
                System.out.println("=== SCHEMA ET DISCRIMINANT ===");
                System.out.println("SINGLE_TABLE lignes=" + count(em, "Vehicule")
                        + ", VOITURE=" + em.createNativeQuery("SELECT vehicule_type FROM Vehicule WHERE id = ?")
                        .setParameter(1, voiture.getId()).getSingleResult());
                System.out.println("JOINED lignes: Employe=" + count(em, "Employe")
                        + ", Developpeur=" + count(em, "Developpeur") + ", Manager=" + count(em, "Manager"));
                System.out.println("TABLE_PER_CLASS lignes: Livre=" + count(em, "Livre")
                        + ", Electronique=" + count(em, "Electronique"));
                Transactions.executer(em, context -> {
                    context.find(Voiture.class, voiture.getId()).setMarque("Toyota Hybrid");
                    context.find(Developpeur.class, dev.getId()).setLangage("Java 17");
                    context.find(Livre.class, livre.getId()).setAuteur("Robert Martin");
                });
                em.clear();
                System.out.println("=== MISE A JOUR ===");
                System.out.println("Voiture marque = " + em.find(Voiture.class, voiture.getId()).getMarque());
                System.out.println("Developpeur langage = " + em.find(Developpeur.class, dev.getId()).getLangage());
                System.out.println("Livre auteur = " + em.find(Livre.class, livre.getId()).getAuteur());
                Transactions.executer(em, context -> {
                    context.remove(context.find(Moto.class, moto.getId()));
                    context.remove(context.find(Manager.class, manager.getId()));
                    context.remove(context.find(Electronique.class, electronique.getId()));
                });
                em.clear();
                System.out.println("=== APRES SUPPRESSION ===");
                lister(em);
                System.out.println("CRUD TERMINE : 1 vehicule, 1 employe, 1 produit restants.");
            } finally { em.close(); }
        } finally { emf.close(); }
    }
    private static Object count(EntityManager em, String table) {
        return em.createNativeQuery("SELECT COUNT(*) FROM " + table).getSingleResult();
    }
    private static void lister(EntityManager em) {
        System.out.println("--- Liste des véhicules ---");
        em.createQuery("SELECT v FROM Vehicule v ORDER BY v.id", Vehicule.class).getResultList().forEach(System.out::println);
        System.out.println("--- Liste des employés ---");
        em.createQuery("SELECT e FROM Employe e ORDER BY e.id", Employe.class).getResultList().forEach(System.out::println);
        System.out.println("--- Liste des produits ---");
        em.createQuery("SELECT p FROM Produit p ORDER BY p.id", Produit.class).getResultList().forEach(System.out::println);
    }
}
