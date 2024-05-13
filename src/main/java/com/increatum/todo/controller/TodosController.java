package com.increatum.todo.controller;

import java.time.Clock;
import java.time.OffsetDateTime;
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
import com.increatum.todo.model.TodoUpdate;
import com.increatum.todo.model.Error;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class TodosController implements TodosApi {

    private final NativeWebRequest request;
    private final TodoDbService dbService;
    private final Clock clock;

    public TodosController(NativeWebRequest request, TodoDbService dbService, Clock clock) {
        this.request = request;
        this.dbService = dbService;
        this.clock = clock;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    @ResponseBody Error handleBadRequest(HttpServletRequest req, Exception ex) {
        return new Error()
                .error(ex.getLocalizedMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(req.getRequestURI())
                .timestamp(OffsetDateTime.now(this.clock));
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
        return this.dbService.insert(todo) ? new ResponseEntity<>(todo, HttpStatus.CREATED) 
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Todo> todoUpdate(Long todoId, TodoUpdate todo) {
        if(this.dbService.update(todoId, todo)) {
            return ResponseEntity.of(this.dbService.getById(todoId));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
