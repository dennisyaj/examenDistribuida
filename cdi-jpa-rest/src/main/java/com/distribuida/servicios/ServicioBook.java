package com.distribuida.servicios;

import com.distribuida.db.Book;

import java.util.List;

public interface ServicioBook {

    boolean insert(Book book);

    List<Book> findAll();

    Book findById(Integer id);

    boolean deleteById(Integer id);

    boolean update(Book book);
}
