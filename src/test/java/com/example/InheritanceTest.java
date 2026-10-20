package com.example;
import com.example.model.singletable.*;
import com.example.model.joined.*;
import com.example.model.tableperclass.*;
import javax.persistence.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class InheritanceTest {
    private EntityManagerFactory emf;
    private EntityManager em;
    @Before public void setup() {
        Map<String,Object> config = new HashMap<>();
        config.put("javax.persistence.jdbc.url", "jdbc:h2:mem:lab4_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        config.put("hibernate.show_sql", "false");
        emf = Persistence.createEntityManagerFactory("hibernate-inheritance", config);
        em = emf.createEntityManager();
    }
    @After public void close() {
        try { if (em != null && em.isOpen()) em.close(); }
        finally { if (emf != null && emf.isOpen()) emf.close(); }
    }
    private long count(String table) {
        return ((Number)em.createNativeQuery("SELECT COUNT(*) FROM " + table).getSingleResult()).longValue();
    }
    @Test public void singleTableDiscriminatorAndPolymorphism() {
        Voiture v = new Voiture("Toyota",4);
        Moto m = new Moto("Harley",true);
        Transactions.executer(em, c -> { c.persist(v); c.persist(m); });
        em.clear();
        assertEquals(2, em.createQuery("SELECT v FROM Vehicule v", Vehicule.class).getResultList().size());
        assertTrue(em.find(Vehicule.class,v.getId()) instanceof Voiture);
        assertTrue(em.find(Vehicule.class,m.getId()) instanceof Moto);
        assertEquals("VOITURE",em.createNativeQuery("SELECT vehicule_type FROM Vehicule WHERE id = ?").setParameter(1,v.getId()).getSingleResult());
        assertEquals(4, em.find(Voiture.class,v.getId()).getNombrePortes());
        assertTrue(em.find(Moto.class,m.getId()).isSidecar());
        assertEquals(2,count("Vehicule"));
    }
    @Test public void joinedTablesAndPolymorphism() {
        Developpeur d = new Developpeur("Alice","Java");
        Manager m = new Manager("Bob",3);
        Transactions.executer(em,c -> { c.persist(d); c.persist(m); });
        em.clear();
        assertEquals(2,count("Employe"));
        assertEquals(1,count("Developpeur"));
        assertEquals(1,count("Manager"));
        assertEquals(2,em.createQuery("SELECT e FROM Employe e",Employe.class).getResultList().size());
        assertTrue(em.find(Employe.class,d.getId()) instanceof Developpeur);
        assertEquals("Java",em.find(Developpeur.class,d.getId()).getLangage());
        assertEquals(3,em.find(Manager.class,m.getId()).getNombreEquipes());
    }
    @Test public void tablePerClassUnionAndInheritedColumns() {
        Livre l = new Livre("Clean Code","Robert C. Martin");
        Electronique e = new Electronique("Smartphone",24);
        Transactions.executer(em,c -> { c.persist(l); c.persist(e); });
        em.clear();
        assertNotEquals(l.getId(),e.getId());
        assertEquals(2,em.createQuery("SELECT p FROM Produit p",Produit.class).getResultList().size());
        assertTrue(em.find(Produit.class,l.getId()) instanceof Livre);
        assertEquals("Clean Code",em.createNativeQuery("SELECT nom FROM Livre").getSingleResult());
        assertEquals("Smartphone",em.createNativeQuery("SELECT nom FROM Electronique").getSingleResult());
        assertEquals(24,em.find(Electronique.class,e.getId()).getGarantie());
        assertEquals(0L,((Number)em.createNativeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_NAME='PRODUIT'").getSingleResult()).longValue());
    }
    @Test public void updateAndDeleteAcrossAllStrategies() {
        Voiture v = new Voiture("Toyota",4);
        Developpeur d = new Developpeur("Alice","Java");
        Livre l = new Livre("Clean Code","Robert");
        Transactions.executer(em,c -> { c.persist(v); c.persist(d); c.persist(l); });
        em.clear();
        Transactions.executer(em,c -> {
            c.find(Voiture.class,v.getId()).setMarque("Toyota Hybrid");
            c.find(Developpeur.class,d.getId()).setLangage("Java 17");
            c.find(Livre.class,l.getId()).setAuteur("Robert Martin");
        });
        em.clear();
        assertEquals("Toyota Hybrid",em.find(Vehicule.class,v.getId()).getMarque());
        assertEquals("Java 17",em.find(Developpeur.class,d.getId()).getLangage());
        assertEquals("Robert Martin",em.find(Livre.class,l.getId()).getAuteur());
        Transactions.executer(em,c -> {
            c.remove(c.find(Vehicule.class,v.getId()));
            c.remove(c.find(Employe.class,d.getId()));
            c.remove(c.find(Produit.class,l.getId()));
        });
        em.clear();
        for(String table : new String[]{"Vehicule","Employe","Developpeur","Livre"}) assertEquals(0,count(table));
    }
    @Test public void failureRollsBackEntireTransaction() {
        assertThrows(IllegalStateException.class,() -> Transactions.executer(em,c -> {
            c.persist(new Voiture("Rollback",2));
            throw new IllegalStateException("Simulated failure");
        }));
        assertFalse(em.getTransaction().isActive());
        assertEquals(0,count("Vehicule"));
    }
}
