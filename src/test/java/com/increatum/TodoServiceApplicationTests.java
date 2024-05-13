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

@SpringBootTest(classes = {TodoServiceApplication.class, FixedClockConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        post("/todos", "create-req.json", "create-res.json", 201);
    }

    @Order(5)
    @Test
    void testCreateDuplicate() {
        post("/todos", "create-req.json", "duplicate-res.json", 400);
    }

    @Order(6)
    @Test
    void testCreateError() {
        post("/todos", "create-error-req.json", "create-error-res.json", 400);
    }

    @Order(7)
    @Test
    void testCreateOther() {
        post("/todos", "create-second-req.json", "create-second-res.json", 201);
    }

    @Order(8)
    @Test
    void testGetAll() {
        getOk("/todos", "all-two-res.json");
    }

    @Order(9)
    @Test
    void testUpdate() {
        patchOk("/todos/1", "update-req.json", "update-res.json");
    }

    @Order(10)
    @Test
    void testUpdateStatus() {
        patchOk("/todos/1", "update-status-req.json", "update-status-res.json");
    }

    @Order(11)
    @Test
    void testUpdateDone() {
        patchOk("/todos/1", "update-desc-req.json", "update-desc-res.json");
    }

    @Order(12)
    @Test
    void testUpdateEmpty() {
        patchOk("/todos/1", "update-empty-req.json", "update-empty-res.json");
    }

    @Order(13)
    @Test
    void testGet() {
        getOk("/todos/1", "get-updated-res.json");
    }

    @Order(14)
    @Test
    void testDelete() {
        delete("/todos/2", 204);
    }

    @Order(15)
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

    void patchOk(String path, String req, String res) {
        patch(path, req, res, 200);
    }

    void post(String path, String req, String res, int code) {
        HttpRequest request = jsonRequest(path)
                .POST(BodyPublishers.ofString(JsonUtil.read(ALL_TESTS + req)))
                .build();
        verify(request, res, code);
    }

    void patch(String path, String req, String res, int code) {
        HttpRequest request = jsonRequest(path)
                .method("PATCH", BodyPublishers.ofString(JsonUtil.read(ALL_TESTS + req)))
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
            assertEquals(code, response.statusCode());
            AssertJson.assertData(JsonUtil.prettyPrint(response.body()), ALL_TESTS + res);
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
