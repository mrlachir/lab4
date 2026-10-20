package com.example;
import javax.persistence.*;
import java.util.function.Consumer;

public final class Transactions {
    private Transactions() {}
    public static void executer(EntityManager em, Consumer<EntityManager> action) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin(); action.accept(em); tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                try { tx.rollback(); } catch (RuntimeException failure) { e.addSuppressed(failure); }
            }
            em.clear();
            throw e;
        }
    }
}
