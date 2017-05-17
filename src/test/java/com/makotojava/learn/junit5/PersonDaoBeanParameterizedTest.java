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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import com.makotojava.learn.junit.Person;
import com.makotojava.learn.junit.PersonDaoBean;
import com.makotojava.learn.junit.TestSpringConfiguration;

@RunWith(JUnitPlatform.class)
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

  static Iterator<Person> personProvider() {
    // Objects need an ID or they cannot be updated in the DB
    for (int aa = 0; aa < TEST_PEOPLE.length; aa++) {
      TEST_PEOPLE[aa].setId((long) aa + 1);
    }
    return Arrays.asList(TEST_PEOPLE).iterator();
  }

  @ParameterizedTest(name = "Test# {index}: Person.toString() -> {0}")
  @DisplayName("Parameterized Test: Find by ID")
  @MethodSource(names = "personProvider")
  public void findById(Person paramPerson) {
    assertNotNull(classUnderTest);
    long id = paramPerson.getId();
    Person personFound = classUnderTest.findById(id);
    assertNotNull(personFound);
    performPersonAssertions(paramPerson.getLastName(), paramPerson.getFirstName(),
        paramPerson.getAge(),
        paramPerson.getEyeColor(), paramPerson.getGender(), personFound);
    DataSource dataSource = (DataSource) ctx.getBean("dataSource");
    if (dataSource instanceof EmbeddedDatabase) {
      ((EmbeddedDatabase) dataSource).shutdown();
    }
  }

  @ParameterizedTest
  @DisplayName("Parameterized Test: Update")
  @MethodSource(names = "personProvider")
  public void update(Person paramPerson) {
    assertNotNull(classUnderTest);
    Person personToUpdate = new Person(paramPerson.getLastName(), paramPerson.getFirstName(),
        paramPerson.getAge() + 10,// Modify age
        paramPerson.getEyeColor(), paramPerson.getGender());
    personToUpdate.setId(paramPerson.getId());
    boolean updateSucceeded = classUnderTest.update(personToUpdate);
    assertTrue(updateSucceeded);
  }

  @ParameterizedTest
  @DisplayName("Parameterized Test: Delete")
  @MethodSource(names = "personProvider")
  public void delete(Person paramPerson) {
    assertNotNull(classUnderTest);
    Person personDeleted = classUnderTest.delete(paramPerson);
    performPersonAssertions(paramPerson.getLastName(), paramPerson.getFirstName(), paramPerson.getAge(),
        paramPerson.getEyeColor(), paramPerson.getGender(), personDeleted);
  }

}
