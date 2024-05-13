package com.increatum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import com.increatum.test.AssertJson;
import com.increatum.test.JsonUtil;
import com.increatum.todo.TodoServiceApplication;
import com.increatum.todo.db.TodoDbService;

@SpringBootTest(classes = {TodoServiceApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TodoServiceApplicationTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    private TodoDbService todoDbService;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static final String ALL_TESTS = "/all-tests/";

    @Order(1)
    @Test
    public void createDb() throws Exception {
        jdbcTemplate.execute("DROP TABLE todos");
        todoDbService.createDb();
    }

    @Order(2)
    @Test
    void testGetEmpty() {
        getOk("/todos", "all-empty-res.json");
    }

    @Order(3)
    @Test
    void test404() {
        verifyNotFound("/todos/1");
    }

    @Order(4)
    @Test
    void testCreate() {
        postOk("/todos", "create-req.json", "create-res.json");
    }

    @Order(5)
    @Test
    void testCreateDuplicate() {
        post("/todos", "create-req.json", "duplicate-res.json", 400);
    }

    @Order(6)
    @Test
    void testCreateOther() {
        postOk("/todos", "create-second-req.json", "create-second-res.json");
    }

    @Order(7)
    @Test
    void testGetAll() {
        getOk("/todos", "all-two-res.json");
    }

    @Order(8)
    @Test
    void testUpdate() {
        putOk("/todos/1", "update-req.json", "update-res.json");
    }

    @Order(9)
    @Test
    void testGet() {
        getOk("/todos/1", "get-updated-res.json");
    }

    @Order(10)
    @Test
    void testDelete() {
        delete("/todos/2", 204);
    }

    @Order(11)
    @Test
    void testDeleteNotFound() {
        delete("/todos/2", 404);
    }

    private void delete(String path, int code) {
        HttpRequest request = jsonRequest(path)
                .DELETE().build();
        verify(request, code);
    }

    void getOk(String path, String res) {
        verifyOk(jsonRequest(path).build(), res);
    }

    void putOk(String path, String req, String res) {
        put(path, req, res, 200);
    }

    void postOk(String path, String req, String res) {
        post(path, req, res, 200);
    }

    void post(String path, String req, String res, int code) {
        HttpRequest request = jsonRequest(path)
                .POST(BodyPublishers.ofString(JsonUtil.read(ALL_TESTS + req)))
                .build();
        verify(request, res, code);
    }

    void put(String path, String req, String res, int code) {
        HttpRequest request = jsonRequest(path)
                .PUT(BodyPublishers.ofString(JsonUtil.read(ALL_TESTS + req)))
                .build();
        verify(request, res, code);
    }

    void verifyNotFound(String path) {
        this.verify(getRequest(path), 404);
    }

    void verifyOk(HttpRequest request, String res) {
        this.verify(request, res, 200);
    }
    
    void verify(HttpRequest request, String res, int code) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            AssertJson.assertData(JsonUtil.prettyPrint(response.body()), ALL_TESTS + res);
            assertEquals(code, response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    void verify(HttpRequest request, int code) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(code, response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private HttpRequest getRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path)).build();
    }

    private HttpRequest.Builder jsonRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .setHeader("Content-Type", "application/json");
    }

}
