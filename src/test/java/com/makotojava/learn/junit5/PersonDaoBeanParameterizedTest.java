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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import com.makotojava.learn.junit.Person;
import com.makotojava.learn.junit.PersonDaoBean;
import com.makotojava.learn.junit.PersonTestEnum;
import com.makotojava.learn.junit.TestSpringConfiguration;

// @RunWith(JUnitPlatform.class)
@DisplayName("Testing PersonDaoBean using parameterized test methods")
@Advanced
/**
 * Parameterized Test for testing PersonDaoBean.
 * 
 * @author J Steven Perry
 *
 */
public class PersonDaoBeanParameterizedTest extends AbstractBaseTest {

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

  /**
   * Provides Person iterator, works with the
   * MethodSource based methods.
   * 
   * @return Iterator<Person> - the Iterator that contains Person
   *         instances for parameterized methods.
   */
  static Iterator<Person> personProvider() {
    return Arrays.asList(PersonTestEnum.toPersonArray()).iterator();
  }

  /**
   * Provides Person iterator, works with the MethodSource
   * based methods.
   * 
   * @return Iterator<Person> - the Iterator that contains Person
   *         instances from PersonTestEnum, in reverse order.
   */
  static Person[] additionalPersonProvider() {
    PersonTestEnum[] testPeople = PersonTestEnum.values();
    Person[] people = new Person[testPeople.length];
    for (int aa = 0, bb = testPeople.length - 1; aa < testPeople.length; aa++, bb--) {
      people[aa] = testPeople[bb].getPerson();
    }
    return people;
  }

  @ParameterizedTest(name = "@MethodSource: FindById(): Test# {index}: Person.toString() -> {0}")
  @DisplayName("FindById using MethodSource")
  @MethodSource(value = { "personProvider", "additionalPersonProvider" })
  public void findById(Person paramPerson) {
    assertNotNull(classUnderTest);
    long id = paramPerson.getId();
    Person personFound = classUnderTest.findById(id);
    assertNotNull(personFound);
    performPersonAssertions(paramPerson.getLastName(), paramPerson.getFirstName(),
        paramPerson.getAge(),
        paramPerson.getEyeColor(), paramPerson.getGender(), personFound);
  }

  @ParameterizedTest(name = "@MethodSource: update(): Test# {index}: Person.toString() -> {0}")
  @DisplayName("Update using MethodSource")
  @MethodSource(value = "personProvider")
  public void update(Person paramPerson) {
    assertNotNull(classUnderTest);
    Person personToUpdate = new Person(paramPerson.getLastName(), paramPerson.getFirstName(),
        paramPerson.getAge() + 10,// Modify age
        paramPerson.getEyeColor(), paramPerson.getGender());
    personToUpdate.withId(paramPerson.getId());
    boolean updateSucceeded = classUnderTest.update(personToUpdate);
    assertTrue(updateSucceeded);
  }

  @ParameterizedTest(name = "@MethodSource: delete(): Test# {index}: Person.toString() -> {0}")
  @DisplayName("Delete using MethodSource")
  @MethodSource(value = "personProvider")
  public void delete(Person paramPerson) {
    assertNotNull(classUnderTest);
    Person personDeleted = classUnderTest.delete(paramPerson);
    performPersonAssertions(paramPerson.getLastName(), paramPerson.getFirstName(), paramPerson.getAge(),
        paramPerson.getEyeColor(), paramPerson.getGender(), personDeleted);
  }

  @Nested
  @DisplayName("When using @ValueSource and @EnumSource")
  public class PersonDaoBeanTest {

    @ParameterizedTest(name = "@ValueSource: FindById(): Test# {index}: Id: {0}")
    @DisplayName("FindById using ValueSource")
    @ValueSource(longs = { 1L, 2L, 3L, 4L, 5L })
    public void findById(Long id) {
      assertNotNull(classUnderTest);
      Person personFound = classUnderTest.findById(id);
      assertNotNull(personFound);
      assertEquals(id, personFound.getId());
    }

    @ParameterizedTest(name = "@ValueSource: FindAllByLastName(): Test# {index}: LastName = {0}")
    @DisplayName("FindAllByLastName using ValueSource")
    @ValueSource(strings = { "Wragdhen", "Jaxl", "Kath", "Yagnag", "Ugzor" })
    public void findAllByLastName(String lastName) {
      assertNotNull(classUnderTest);
      List<Person> peopleFound = classUnderTest.findAllByLastName(lastName);
      assertNotNull(peopleFound);
      assertAll(
          () -> assertFalse(peopleFound.isEmpty()),
          () -> assertEquals(1, peopleFound.size()),
          () -> assertEquals(lastName, peopleFound.get(0).getLastName()));
    }

    @ParameterizedTest(name = "@EnumSource: FindById(): Test# {index}: PersonTestEnum -> {0}")
    @DisplayName("FindById using EnumSource")
    @EnumSource(PersonTestEnum.class)
    public void findById(PersonTestEnum testPerson) {
      assertNotNull(classUnderTest);
      Person person = testPerson.getPerson();
      Person personFound = classUnderTest.findById(person.getId());
      assertNotNull(personFound);
      performPersonAssertions(person.getLastName(), person.getFirstName(), person.getAge(), person.getEyeColor(),
          person.getGender(), personFound);
    }

  }

}
