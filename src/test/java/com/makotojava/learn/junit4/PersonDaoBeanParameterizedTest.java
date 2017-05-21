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
package com.makotojava.learn.junit4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import com.makotojava.learn.junit.Person;
import com.makotojava.learn.junit.PersonDaoBean;
import com.makotojava.learn.junit.PersonGenerator;
import com.makotojava.learn.junit.TestSpringConfiguration;

/**
 * Demonstrates Parameterized Tests using JUnit 4.
 * 
 * @author J Steven Perry
 *
 */
@RunWith(org.junit.runners.Parameterized.class)
public class PersonDaoBeanParameterizedTest {

  private ApplicationContext ctx;

  private PersonDaoBean classUnderTest;

  @Before
  public void setUp() throws Exception {
    ctx = new AnnotationConfigApplicationContext(TestSpringConfiguration.class);
    classUnderTest = ctx.getBean(PersonDaoBean.class);
  }

  @After
  public void tearDown() throws Exception {
    DataSource dataSource = (DataSource) ctx.getBean("dataSource");
    if (dataSource instanceof EmbeddedDatabase) {
      ((EmbeddedDatabase) dataSource).shutdown();
    }
  }

  @Parameters(name = "{index}: {0}")
  public static Collection<Person> parameters() {
    Person[] people = new Person[] {
        new Person("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE),
        new Person("Jaxl", "Lar", 21, PersonGenerator.BROWN, PersonGenerator.MALE),
        new Person("Kath", "Fon", 35, PersonGenerator.GOLD, PersonGenerator.FEMALE),
        new Person("Yagnag", "Mog", 18, PersonGenerator.BROWN, PersonGenerator.FEMALE),
        new Person("Ugzor", "Sar", 11, PersonGenerator.HAZEL, PersonGenerator.UNKNOWN),
    };
    // Objects need an ID or they cannot be updated in the DB
    for (int aa = 0; aa < people.length; aa++) {
      people[aa].withId((long) aa + 1);
    }
    return Arrays.asList(people);
  }

  private Person paramPerson;

  public PersonDaoBeanParameterizedTest(Person paramPerson) {
    this.paramPerson = paramPerson;
  }

  @Test
  public void findById() {
    assertNotNull(classUnderTest);
    long id = paramPerson.getId();
    Person personFound = classUnderTest.findById(id);
    assertNotNull(personFound);
    performPersonAssertions(paramPerson.getLastName(), paramPerson.getFirstName(),
        paramPerson.getAge(),
        paramPerson.getEyeColor(), paramPerson.getGender(), personFound);
  }

  @Test
  public void update() {
    assertNotNull(classUnderTest);
    Person personToUpdate = new Person(paramPerson.getLastName(), paramPerson.getFirstName(),
        paramPerson.getAge() + 10,// Modify age
        paramPerson.getEyeColor(), paramPerson.getGender());
    personToUpdate.withId(paramPerson.getId());
    boolean updateSucceeded = classUnderTest.update(personToUpdate);
    assertTrue(updateSucceeded);
  }

  @Test
  public void delete() {
    assertNotNull(classUnderTest);
    Person personDeleted = classUnderTest.delete(paramPerson);
    performPersonAssertions(paramPerson.getLastName(), paramPerson.getFirstName(), paramPerson.getAge(),
        paramPerson.getEyeColor(), paramPerson.getGender(), personDeleted);
  }

  // TODO: Refactor into base class
  private void performPersonAssertions(String lastName, String firstName, int age, String eyeColor, String gender,
      Person person) {
    assertEquals(firstName, person.getFirstName());
    assertEquals(lastName, person.getLastName());
    assertEquals(age, person.getAge());
    assertEquals(eyeColor.toString(), person.getEyeColor());
    assertEquals(gender.toString(), person.getGender());
  }

}
