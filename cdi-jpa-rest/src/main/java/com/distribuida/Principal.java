package com.distribuida;

import com.distribuida.db.Book;
import com.distribuida.servicios.ServicioBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import spark.Request;
import spark.Response;

import java.util.List;

import static spark.Spark.*;

public class Principal {
    static SeContainer container;

    static List<Book> listarBooks(Request req, Response res) {
        var servicio = container.select(ServicioBook.class)
                .get();
        res.type("application/json");

        return servicio.findAll();
    }

    static Book buscarBook(Request req, Response res) {
        var servicio = container.select(ServicioBook.class)
                .get();
        res.type("application/json");

        String _id = req.params(":id");
        var book = servicio.findById(Integer.valueOf(_id));

        if (book == null) {
            halt(404, "Book no encontrado");
        }

        return book;
    }

    static boolean borrarBook(Request req, Response res) {
        var servicio = container.select(ServicioBook.class)
                .get();
        res.type("application/json");

        String _id = req.params(":id");
        boolean borradoExitoso = servicio.deleteById(Integer.valueOf(_id));

        if (!borradoExitoso) {
            halt(404, "Book no borrada");
        } else {
            res.status(200);
            res.body("Book borrada exitosamente");
        }
        return borradoExitoso;
    }

    static boolean insertarBook(Request req, Response res) throws JsonProcessingException {
        var servicio = container.select(ServicioBook.class)
                .get();
        res.type("application/json");

        String requestBody = req.body();
        System.out.println(requestBody);

        ObjectMapper objectMapper = new ObjectMapper();
        Book book = objectMapper.readValue(requestBody, Book.class);


        boolean insertadoExitoso = servicio.insert(book);

        if (!insertadoExitoso) {
            halt(404, "No se puede insertar");
        } else {
            res.status(200);
            res.body("Book insertado exitosamente");
        }

        return insertadoExitoso;
    }

    static boolean actualizarBook(Request req, Response res) throws JsonProcessingException {
        var servicio = container.select(ServicioBook.class)
                .get();
        res.type("application/json");

        String _id = req.params(":id");
        String requestBody = req.body();

        ObjectMapper objectMapper = new ObjectMapper();
        Book book = objectMapper.readValue(requestBody, Book.class);
        book.setId(Integer.valueOf(_id));

        boolean actualizacionExitoso = servicio.update(book);

        if (!actualizacionExitoso) {
            halt(404, "No se puede actualizar");
        } else {
            res.status(200);
            res.body("Book actualizado");
        }
        return actualizacionExitoso;
    }


    public static void main(String[] args) {

        container = SeContainerInitializer
                .newInstance()
                .initialize();

        ServicioBook servicio = container.select(ServicioBook.class)
                .get();

        port(8080);

        configureCors();
        ObjectMapper objectMapper = new ObjectMapper();

        get("/books", Principal::listarBooks, objectMapper::writeValueAsString);
        get("/books/:id", Principal::buscarBook, objectMapper::writeValueAsString);
        post("/books", Principal::insertarBook, objectMapper::writeValueAsString);
        put("/books/:id", Principal::actualizarBook, objectMapper::writeValueAsString);
        delete("/books/:id", Principal::borrarBook);
    }

    static void configureCors() {
        before((request, response) -> {
            // Configuración CORS
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
            response.header("Access-Control-Allow-Credentials", "true");
        });

        options("/*", (request, response) -> {
            response.status(200);
            return "OK";
        });
    }
}
