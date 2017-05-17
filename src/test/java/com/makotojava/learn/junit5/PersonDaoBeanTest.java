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
 *
 */
package com.makotojava.learn.junit5;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.List;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import com.makotojava.learn.junit.Person;
import com.makotojava.learn.junit.PersonDaoBean;
import com.makotojava.learn.junit.PersonGenerator;
import com.makotojava.learn.junit.TestSpringConfiguration;
import com.makotojava.learn.junit.TestSpringConfigurationEmptyDb;

/**
 * Test class for PersonDaoBean.
 * 
 * Uses JUnit Jupiter API
 * 
 * @author J Steven Perry
 *
 */
@DisplayName("Testing PersonDaoBean")
@ExtendWith(JUnit5ExtensionShowcase.class)
public class PersonDaoBeanTest extends AbstractBaseTest {

  @Nested
  @DisplayName("When Database is populated with objects")
  public class WhenDatabaseIsPopulated {

    private ApplicationContext ctx;

    private PersonDaoBean classUnderTest;

    @BeforeEach
    public void setUp() throws Exception {
      ctx = new AnnotationConfigApplicationContext(TestSpringConfiguration.class);
      classUnderTest = ctx.getBean(PersonDaoBean.class);
    }

    @AfterEach
    public void tearDown() throws Exception {
      DataSource dataSource = (DataSource) ctx.getBean("dataSource");
      if (dataSource instanceof EmbeddedDatabase) {
        ((EmbeddedDatabase) dataSource).shutdown();
      }
    }

    @Test
    @DisplayName("Find all objects in the database")
    public void findAll() {
      assertNotNull(classUnderTest, "PersonDaoBean reference cannot be null.");
      List<Person> people = classUnderTest.findAll();
      assertAll("Returned list should not be null, empty, and must contain 5 objects",
          () -> assertNotNull(people),
          () -> assertFalse(people.isEmpty()),
          () -> assertEquals(5, people.size()));
    }

    @Test
    @DisplayName("Find Objects by ID")
    public void findById() {
      assertNotNull(classUnderTest, "PersonDaoBean reference cannot be null.");
      assertAll("Assert found objects by IDs 1-5",
          () -> {
            Person person = classUnderTest.findById(1L);
            assertNotNull(person, "Person reference was null");
            performPersonAssertions("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE, person);
          },
          () -> {
            Person person = classUnderTest.findById(2L);
            assertNotNull(person, "Person reference was null");
            performPersonAssertions("Jaxl", "Lar", 21, PersonGenerator.BROWN, PersonGenerator.MALE, person);
          },
          () -> {
            Person person = classUnderTest.findById(3L);
            assertNotNull(person, "Person reference was null");
            performPersonAssertions("Kath", "Fon", 35, PersonGenerator.GOLD, PersonGenerator.FEMALE, person);
          },
          () -> {
            Person person = classUnderTest.findById(4L);
            assertNotNull(person, "Person reference was null");
            performPersonAssertions("Yagnag", "Mog", 18, PersonGenerator.BROWN, PersonGenerator.FEMALE, person);
          }, () -> {
            Person person = classUnderTest.findById(5L);
            assertNotNull(person, "Person reference was null");
            performPersonAssertions("Ugzor", "Sar", 11, PersonGenerator.HAZEL, PersonGenerator.UNKNOWN, person);
          });
    }

    /**
     * Create same tests as in findById, but create them as DynamicTest instances.
     * 
     * @return
     */
    @Advanced
    @TestFactory
    @DisplayName("FindById - Dynamic Test Generator")
    Stream<DynamicTest> generateFindByIdDynamicTests() {
      Long[] ids = { 1L, 2L, 3L, 4L, 5L };
      return Stream.of(ids).map(id -> dynamicTest("DynamicTest: Find by ID " + id, () -> {
        Person person = classUnderTest.findById(id);
        assertNotNull(person);
        int index = id.intValue() - 1;
        performPersonAssertions(TEST_PEOPLE[index].getLastName(), TEST_PEOPLE[index].getFirstName(),
            TEST_PEOPLE[index].getAge(), TEST_PEOPLE[index].getEyeColor(), TEST_PEOPLE[index].getGender(), person);
      }));
    }

    @Test
    @DisplayName("Find all objects by a specific last name")
    public void findAllByLastName() {
      assertNotNull(classUnderTest, "PersonDaoBean reference cannot be null.");
      assertAll(
          () -> {
            List<Person> people = classUnderTest.findAllByLastName("Wragdhen");
            assertNotNull(people);
            assertFalse(people.isEmpty());
            assertEquals(1, people.size());
            Person person = people.get(0);
            performPersonAssertions("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE, person);
          },
          () -> {
            List<Person> people = classUnderTest.findAllByLastName("Jaxl");
            assertNotNull(people);
            assertFalse(people.isEmpty());
            assertEquals(1, people.size());
            Person person = people.get(0);
            performPersonAssertions("Jaxl", "Lar", 21, PersonGenerator.BROWN, PersonGenerator.MALE, person);
          });
    }

    @Test
    @DisplayName("Add generated Person should succeed - uses Parameter injection")
    @ExtendWith(GeneratedPersonParameterResolver.class)
    public void add(Person person) {
      assertNotNull(classUnderTest, "PersonDaoBean reference cannot be null.");
      Person personAdded = classUnderTest.add(person);
      assertNotNull(personAdded, "Add failed but should have succeeded");
      assertNotNull(personAdded.getId());
      performPersonAssertions(person.getLastName(), person.getFirstName(), person.getAge(), person.getEyeColor(),
          person.getGender(), personAdded);
    }

    @Test
    @DisplayName("Adding a duplicate Person should fail")
    public void add_duplicate() {
      assertNotNull(classUnderTest, "PersonDaoBean reference cannot be null.");
      // Create new local Person object whose attributes match one that exists in the DB
      Person person = new Person("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE);
      Person personAdded = classUnderTest.add(person);
      assertNull(personAdded, "Add succeeded but should have failed.");
    }

    @Test
    @DisplayName("Update existing Person should succeed")
    public void update() {
      assertNotNull(classUnderTest, "PersonDaoBean reference cannot be null.");
      Person person = new Person("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE);
      person.setId(1L);
      boolean updateSucceeded = classUnderTest.update(person);
      assertTrue(updateSucceeded);
    }

    @Test
    @DisplayName("Update Person with same first/last name as another should fail")
    public void update_causesDuplicate() {
      assertNotNull(classUnderTest, "PersonDaoBean reference cannot be null.");
      // This Person is actually ID 2, but try and update it as ID 1. The result is
      /// a duplicate, which should fail.
      Person person = new Person("Jaxl", "Lar", 21, PersonGenerator.BROWN, PersonGenerator.MALE);
      person.setId(1L);
      boolean updateSucceeded = classUnderTest.update(person);
      assertFalse(updateSucceeded);
    }

    @Test
    @DisplayName("Deleting existing Person should succeed")
    public void delete() {
      assertNotNull(classUnderTest, "PersonDaoBean reference cannot be null.");
      Person person = new Person("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE);
      person.setId(1L);
      Person personDeleted = classUnderTest.delete(person);
      assertNotNull(personDeleted);
      performPersonAssertions(person.getLastName(), person.getFirstName(), person.getAge(), person.getEyeColor(),
          person.getGender(), personDeleted);
    }

  }

  @Nested
  @DisplayName("When Database is empty")
  public class WhenDatabaseIsEmpty {

    private ApplicationContext ctx;

    private PersonDaoBean classUnderTest;

    @BeforeEach
    void setUp() {
      ctx = new AnnotationConfigApplicationContext(TestSpringConfigurationEmptyDb.class);
      classUnderTest = ctx.getBean(PersonDaoBean.class);
    }

    @AfterEach
    void tearDown() throws Exception {
      DataSource dataSource = (DataSource) ctx.getBean("dataSource");
      if (dataSource instanceof EmbeddedDatabase) {
        ((EmbeddedDatabase) dataSource).shutdown();
      }
    }

    @Test
    @DisplayName("findAll should return empty list")
    public void findAll_WithEmptyDatabase() {
      assertNotNull(classUnderTest, "PersonDaoBean reference cannot be null.");
      List<Person> people = classUnderTest.findAll();
      assertNotNull(people);
      assertAll(
          () -> assertTrue(people.isEmpty()),
          () -> assertEquals(0, people.size()));
    }

    @Test
    @DisplayName("Update should fail because object does not exist")
    public void update_WithEmptyDatabase() {
      assertNotNull(classUnderTest, "PersonDaoBean reference cannot be null.");
      Person person = new Person("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE);
      person.setId(1L);
      boolean updateSucceeded = classUnderTest.update(person);
      assertFalse(updateSucceeded, "Update succeeded but should have failed.");

    }

    @Test
    @DisplayName("Delete should fail because object does not exist")
    public void delete_WithEmptyDatabase() {
      assertNotNull(classUnderTest, "PersonDaoBean reference cannot be null!");
      Person person = new Person("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE);
      person.setId(1L);
      Person personDeleted = classUnderTest.delete(person);
      assertNull(personDeleted, "Delete succeeded but should have failed.");
    }

  }

}
