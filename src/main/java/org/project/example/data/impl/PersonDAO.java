package org.project.example.data.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.project.example.data.DAOConnectionManager;
import org.project.example.model.Person;

public class PersonDAO {

    private DAOConnectionManager connection;

    public PersonDAO(DAOConnectionManager connection) {
        this.connection = connection;
    }

    public Person createPerson(Person person) throws SQLException {
        String sql = new StringBuilder()
            .append("INSERT INTO person (name) ")
            .append("VALUES (?);").toString();

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, person.getName());
            
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating person failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    person.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating person failed, no ID obtained.");
                }
            }
        }
        return person;
    }

    public Person findPersonById(long id) throws SQLException {
        Person person = null;
        String sql = new StringBuilder()
            .append("SELECT name ")
            .append("FROM person ")
            .append("WHERE person_id = ?").toString();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person();
                person.setId(id);
                person.setName(rs.getString("name"));
            } 
        }
        return person;
    }

    public void updatePerson(Person person) throws SQLException {
        String sql = new StringBuilder()
            .append("UPDATE person ")
            .append("SET name=? ")
            .append("WHERE person_id=?").toString();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, person.getName());
            stmt.setLong(2,person.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new NoSuchElementException("Update person failed, no rows affected.");
            }
        }
    }

    public void deletePersonById(Long id) throws SQLException {
        String sql = new StringBuilder()
            .append("DELETE FROM person ")
            .append("WHERE person_id=?").toString();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1,id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new NoSuchElementException("Delete person failed, no rows affected.");
            }
        }
    }

    public List<Person> listAllPerson() throws SQLException {
        List<Person> persons = new ArrayList<Person>();
        String sql = new StringBuilder()
            .append("SELECT person_id,name ")
            .append("FROM person ").toString();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Person person = new Person();
                person.setId(rs.getLong("person_id"));
                person.setName(rs.getString("name"));
                persons.add(person);
            } 
        }
        return persons;
    }


}