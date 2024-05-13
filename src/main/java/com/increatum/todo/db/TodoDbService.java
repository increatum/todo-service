package com.increatum.todo.db;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.increatum.todo.model.Todo;
import com.increatum.todo.model.TodoUpdate;

import jakarta.annotation.PostConstruct;

@Service
public class TodoDbService {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private RowMapper<Todo> rowMapper = (rs, rowNum) -> {
        Todo todo = new Todo();
        todo.setId(rs.getLong("id"));
        todo.setDescription(rs.getString("description"));
        todo.setCompleted(rs.getBoolean("completed"));
        return todo;
    };

    @PostConstruct
    public void createDb() throws Exception {
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS todos(id INTEGER(64) PRIMARY KEY, description VARCHAR(100), completed BOOLEAN)");
    }

    public List<Todo> getAll() {
        return jdbcTemplate.query("SELECT id, description, completed FROM todos", rowMapper);
    }

    public Optional<Todo> getById(Long id) {
        try {
            return Optional.of(jdbcTemplate.queryForObject("SELECT id, description, completed FROM todos where id=" + id, rowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean deleteById(Long id) {
        return 1 == jdbcTemplate.update("DELETE FROM todos where id=?", id);
    }

    public boolean insert(Todo todo) {
        return 1 == jdbcTemplate.update("INSERT INTO todos(id, description, completed) values(?, ?, ?)", todo.getId(),
                todo.getDescription(), todo.getCompleted());
    }

    public boolean update(Long todoId, TodoUpdate todo) {
        Map<String, Object> params = new TreeMap<>();
        if(todo.getCompleted() != null) {
            params.put("completed=?", todo.getCompleted());
        }
        if(todo.getDescription() != null) {
            params.put("description=?", todo.getDescription());
        }
        if(params.size() == 0) {
            return true; // TODO nothing to update or validation error?
        }
        String updateSql = new StringBuilder("UPDATE todos SET ")
                .append(String.join(",", params.keySet()))
                .append(" where id=?").toString();
        params.put("id", todoId);
        return 1 == jdbcTemplate.update(updateSql, params.values().toArray());
    }

}
