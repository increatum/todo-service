package com.increatum.todo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import com.increatum.todo.api.TodosApi;
import com.increatum.todo.db.TodoDbService;
import com.increatum.todo.model.Todo;
import com.increatum.todo.model.Error;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class TodosController implements TodosApi {

    private final NativeWebRequest request;
    private final TodoDbService dbService;

    public TodosController(NativeWebRequest request, TodoDbService dbService) {
        this.request = request;
        this.dbService = dbService;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    @ResponseBody Error handleBadRequest(HttpServletRequest req, Exception ex) {
        return new Error().message(ex.getLocalizedMessage()).code(-1);
    } 
    
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(this.request);
    }

    @Override
    public ResponseEntity<List<Todo>> todosGetAll() {
        return ResponseEntity.ok(this.dbService.getAll());
    }

    @Override
    public ResponseEntity<Void> todoDelete(Long todoId) {
        return this.dbService.deleteById(todoId) ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Todo> todoRead(Long todoId) {
        return ResponseEntity.of(this.dbService.getById(todoId));
    }

    @Override
    public ResponseEntity<Todo> todoCreate(Todo todo) {
        return this.dbService.insert(todo) ? ResponseEntity.ok(todo) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Todo> todoUpdate(Long todoId, Todo todo) {
        todo.setId(todoId);
        return this.dbService.update(todo) ? ResponseEntity.ok(todo) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
