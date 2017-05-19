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

import java.util.Random;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.makotojava.learn.junit.Person;

/**
 * Returns a Random Person object from the ones that are pre-defined to
 * exist in the Database.
 * 
 * @author J Steven Perry
 *
 */
public class RandomExistingPersonParameterResolver implements ParameterResolver {

  @Override
  public boolean supports(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType() == Person.class;
  }

  @Override
  public Object resolve(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    Person ret = null;
    // Random number generator
    Random random = new Random();
    int randomIndex = random.nextInt(5);// New random int between 0 - 5
    ret = AbstractBaseTest.TEST_PEOPLE[randomIndex];
    // The index is the same as the ID in the DB
    ret.setId((long) randomIndex + 1);
    // Return that Person object
    return ret;
  }

}
