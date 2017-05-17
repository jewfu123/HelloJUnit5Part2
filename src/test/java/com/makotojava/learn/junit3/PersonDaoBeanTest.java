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
package com.makotojava.learn.junit3;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import com.makotojava.learn.junit.Person;
import com.makotojava.learn.junit.PersonDaoBean;
import com.makotojava.learn.junit.PersonGenerator;
import com.makotojava.learn.junit.TestSpringConfiguration;
import com.makotojava.learn.junit.TestSpringConfigurationEmptyDb;

import junit.framework.TestCase;

/**
 * Test class for PersonDaoBean.
 * 
 * Uses JUnit 3 API
 * 
 * @author J Steven Perry
 *
 */
public class PersonDaoBeanTest extends TestCase {

  private ApplicationContext ctx;

  private PersonDaoBean classUnderTest;

  @Override
  protected void setUp() throws Exception {
    ctx = new AnnotationConfigApplicationContext(TestSpringConfiguration.class);
    classUnderTest = ctx.getBean(PersonDaoBean.class);
  }

  @Override
  protected void tearDown() throws Exception {
    DataSource dataSource = (DataSource) ctx.getBean("dataSource");
    if (dataSource instanceof EmbeddedDatabase) {
      ((EmbeddedDatabase) dataSource).shutdown();
    }
  }

  public void testFindAll() {
    assertNotNull(classUnderTest);
    List<Person> people = classUnderTest.findAll();
    assertNotNull(people);
    assertFalse(people.isEmpty());
    assertEquals(5, people.size());
  }

  public void testFindAll_WithEmptyDatabase() {
    ctx = new AnnotationConfigApplicationContext(TestSpringConfigurationEmptyDb.class);
    assertNotNull(classUnderTest);
    List<Person> people = classUnderTest.findAll();
    assertNotNull(people);
    assertTrue(people.isEmpty());
    assertEquals(0, people.size());
  }

  public void testUpdatePerson_WithEmptyDatabase() {
    ctx = new AnnotationConfigApplicationContext(TestSpringConfigurationEmptyDb.class);
    assertNotNull(classUnderTest);
    Person person = new Person("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE);
    person.setId(1L);
    boolean updateSucceeded = classUnderTest.update(person);
    assertFalse(updateSucceeded);

  }

  public void testDeletePerson_WithEmptyDatabase() {
    ctx = new AnnotationConfigApplicationContext(TestSpringConfigurationEmptyDb.class);
    assertNotNull(classUnderTest);
    Person person = new Person("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE);
    person.setId(1L);
    Person personDeleted = classUnderTest.delete(person);
    assertNull(personDeleted);
  }

  public void testFindById() {
    assertNotNull(classUnderTest);
    Person person = classUnderTest.findById(1L);
    assertNotNull(person);
    performPersonAssertions("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE, person);
    person = classUnderTest.findById(2L);
    performPersonAssertions("Jaxl", "Lar", 21, PersonGenerator.BROWN, PersonGenerator.MALE, person);
    person = classUnderTest.findById(3L);
    performPersonAssertions("Kath", "Fon", 35, PersonGenerator.GOLD, PersonGenerator.FEMALE, person);
    person = classUnderTest.findById(4L);
    performPersonAssertions("Yagnag", "Mog", 18, PersonGenerator.BROWN, PersonGenerator.FEMALE, person);
    person = classUnderTest.findById(5L);
    performPersonAssertions("Ugzor", "Sar", 11, PersonGenerator.HAZEL, PersonGenerator.UNKNOWN, person);
  }

  public void testFindAllByLastName() {
    assertNotNull(classUnderTest);
    List<Person> people = classUnderTest.findAllByLastName("Wragdhen");
    assertNotNull(people);
    assertFalse(people.isEmpty());
    assertEquals(1, people.size());
    Person person = people.get(0);
    performPersonAssertions("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE, person);
    people = classUnderTest.findAllByLastName("Jaxl");
    assertNotNull(people);
    assertFalse(people.isEmpty());
    assertEquals(1, people.size());
    person = people.get(0);
    performPersonAssertions("Jaxl", "Lar", 21, PersonGenerator.BROWN, PersonGenerator.MALE, person);
  }

  public void testAdd() {
    assertNotNull(classUnderTest);
    Person person = PersonGenerator.createPerson();
    Person personAdded = classUnderTest.add(person);
    assertNotNull(personAdded);
    assertNotNull(personAdded.getId());
    performPersonAssertions(person.getLastName(), person.getFirstName(), person.getAge(), person.getEyeColor(),
        person.getGender(), personAdded);
  }

  public void testAdd_duplicate() {
    assertNotNull(classUnderTest);
    // Create new local Person object whose attributes match one that exists in the DB
    Person person = new Person("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE);
    Person personAdded = classUnderTest.add(person);
    assertNull(personAdded);
  }

  public void testUpdate() {
    assertNotNull(classUnderTest);
    Person person = new Person("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE);
    person.setId(1L);
    boolean updateSucceeded = classUnderTest.update(person);
    assertTrue(updateSucceeded);
  }

  public void testDelete() {
    assertNotNull(classUnderTest);
    Person person = new Person("Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE);
    person.setId(1L);
    Person personDeleted = classUnderTest.delete(person);
    assertNotNull(personDeleted);
    performPersonAssertions(person.getLastName(), person.getFirstName(), person.getAge(), person.getEyeColor(),
        person.getGender(), personDeleted);
  }

  private void performPersonAssertions(String lastName, String firstName, int age, String eyeColor, String gender,
      Person person) {
    assertEquals(firstName, person.getFirstName());
    assertEquals(lastName, person.getLastName());
    assertEquals(age, person.getAge());
    assertEquals(eyeColor.toString(), person.getEyeColor());
    assertEquals(gender.toString(), person.getGender());
  }

}
