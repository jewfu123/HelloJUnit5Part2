/*
 * Copyright 2017 Makoto Consulting Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.makotojava.learn.junit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * Spring Bean implementation of the PersonDao interface.
 * 
 * @author J Steven Perry
 *
 */
@Component
public class PersonDaoBean implements PersonDao {

  private static final Logger log = Logger.getLogger(PersonDaoBean.class);

  @Autowired
  private DataSource dataSource;

  /**
   * NPE preventer. NEVER use a raw class-level reference. Use
   * this getter instead.
   */
  private DataSource getDataSource() {
    if (dataSource == null) {
      throw new RuntimeException("DataSource is null (configuration error, perhaps?)");
    }
    return dataSource;
  }

  @Override
  public List<Person> findAll() {
    List<Person> ret = null;
    JdbcTemplate jdbc = new JdbcTemplate(getDataSource());
    String sql = "SELECT * FROM " + Person.TABLE_NAME;
    ret = jdbc.query(sql, new PersonRowMapper());
    log.info("Found " + ret.size() + " rows from query");
    return ret;

  }

  @Override
  public Person findById(Long id) {
    Person ret = null;
    JdbcTemplate jdbc = new JdbcTemplate(getDataSource());
    String sql = "SELECT * FROM " + Person.TABLE_NAME + " WHERE id = ?";
    Object[] args = { id };
    List<Person> people = jdbc.query(sql, args, new PersonRowMapper());
    if (people != null && !people.isEmpty()) {
      ret = people.get(0);
    }
    return ret;
  }

  @Override
  public List<Person> findAllByLastName(String lastName) {
    List<Person> ret = null;
    JdbcTemplate jdbc = new JdbcTemplate(getDataSource());
    String sql = "SELECT * FROM " + Person.TABLE_NAME + " WHERE last_name = ?";
    Object[] args = { lastName };
    ret = jdbc.query(sql, args, new PersonRowMapper());
    log.info("Found " + ret.size() + " rows from query");
    return ret;
  }

  @Override
  public Person add(Person person) {
    Person ret = null;
    JdbcTemplate jdbc = new JdbcTemplate(getDataSource());
    String sql =
        "INSERT INTO " + Person.TABLE_NAME + "(last_name, first_name, age, eye_color, gender) VALUES(?, ?, ?, ?, ?)";
    Object[] paramValues =
        { person.getLastName(), person.getFirstName(), person.getAge(), person.getEyeColor(), person.getGender() };
    try {
      int numRowsAffected = jdbc.update(sql, paramValues);
      if (numRowsAffected == 1) {
        // Find the Person we just added. equals() should do the trick.
        List<Person> people = findAllByLastName(person.getLastName());
        for (Person p : people) {
          if (p.equals(person)) {
            ret = p;
            break;
          }
        }
      } else {
        String message = "Expected 1 row to be affected by INSERT, instead " + numRowsAffected
            + " were affected (DB configuration error, maybe?)";
        log.error(message);
      }
    } catch (DataAccessException e) {
      String message = "Exception occurred while inserting record";
      log.error(message, e);
    }
    return ret;
  }

  @Override
  public boolean update(Person person) {
    boolean ret = false;
    JdbcTemplate jdbc = new JdbcTemplate(getDataSource());
    String sql =
        "UPDATE " + Person.TABLE_NAME
            + " SET last_name = ?, first_name = ?, age = ?, eye_color = ?, gender = ? WHERE id = ?";
    Object[] paramValues =
        { person.getLastName(), person.getFirstName(), person.getAge(), person.getEyeColor(), person.getGender(),
            person.getId() };
    try {
      int numRowsAffected = jdbc.update(sql, paramValues);
      if (numRowsAffected == 1) {
        ret = true;
      } else {
        String message = "Expected 1 row to be affected by UPDATE, instead " + numRowsAffected
            + " were affected (DB configuration error, maybe?)";
        log.error(message);
      }
    } catch (DataAccessException e) {
      String message = "Exception occurred while inserting record";
      log.error(message, e);
    }
    return ret;
  }

  @Override
  public Person delete(Person person) {
    Person ret = null;
    JdbcTemplate jdbc = new JdbcTemplate(getDataSource());
    String sql = "DELETE FROM " + Person.TABLE_NAME + " WHERE id = ?";
    Object[] args = { person.getId() };
    int numRowsAffected = jdbc.update(sql, args);
    if (numRowsAffected == 1) {
      ret = person;
    } else {
      String message = "Expected 1 row to be affected by DELETE, instead " + numRowsAffected
          + " were affected (DB configuration error, maybe?)";
      log.error(message);
    }
    return ret;
  }

  private class PersonRowMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
      Person ret = new Person();
      ret.withId(rs.getLong("id"));
      ret.setAge(rs.getInt("age"));
      ret.setEyeColor(rs.getString("eye_color"));
      ret.setGender(rs.getString("gender"));
      ret.setLastName(rs.getString("last_name"));
      ret.setFirstName(rs.getString("first_name"));
      return ret;
    }

  }

}
