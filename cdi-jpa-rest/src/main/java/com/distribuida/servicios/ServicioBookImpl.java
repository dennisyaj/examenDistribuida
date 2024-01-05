package com.distribuida.servicios;

import com.distribuida.db.Book;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class ServicioBookImpl implements ServicioBook {
    @Inject
    EntityManager em;

    @Override
    public boolean insert(Book book) {

        var tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(book);
            tx.commit();
            return true;
        } catch (Exception ex) {
            tx.rollback();
            return false;
        }

    }

    @Override
    public List<Book> findAll() {
        return em.createQuery("select b from Book b").getResultList();
    }

    @Override
    public Book findById(Integer id) {
        return em.find(Book.class, id);
    }

    @Override
    public boolean deleteById(Integer id) {
        Book book = this.findById(id);
        var tx = em.getTransaction();

        try {
            tx.begin();
            em.remove(book);
            tx.commit();
            return true;
        } catch (Exception ex) {
            tx.rollback();
            return false;
        }
    }

    @Override
    public boolean update(Book book) {
        var tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(book);
            tx.commit();
            return true;
        } catch (Exception ex) {
            tx.rollback();
            return false;
        }
    }
}
