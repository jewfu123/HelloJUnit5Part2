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

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import com.makotojava.learn.junit.Person;
import com.makotojava.learn.junit.PersonDaoBean;
import com.makotojava.learn.junit.PersonGenerator;
import com.makotojava.learn.junit.TestSpringConfigurationEmptyDb;

@Advanced
@DisplayName("Testing PersonDaoBean using Repeated Tests")
/**
 * Repeated Test for testing PersonDaoBean.
 * 
 * @author J Steven Perry
 *
 */
public class PersonDaoBeanRepeatedTest extends AbstractBaseTest {

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

  @RepeatedTest(value = 5, name = "{displayName}: iteration {currentRepetition} of {totalRepetitions}")
  @DisplayName("Add generated Person object - repeated test")
  public void add() {
    assertNotNull(classUnderTest, "PersonDaoBean reference cannot be null.");
    Person person = PersonGenerator.createPerson();
    Person personAdded = classUnderTest.add(person);
    assertNotNull(personAdded, "Add failed but should have succeeded");
    assertNotNull(personAdded.getId());
    performPersonAssertions(person.getLastName(), person.getFirstName(), person.getAge(), person.getEyeColor(),
        person.getGender(), personAdded);
  }

}
