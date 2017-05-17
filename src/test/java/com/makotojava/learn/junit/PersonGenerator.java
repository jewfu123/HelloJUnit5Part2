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

import java.util.Random;

/**
 * PersonGenerator. See, when two programs love each other very much...
 * 
 * @author J Steven Perry
 *
 */
public class PersonGenerator {
  private static final int AGE_MAX = 50;

  private static Random rng = new Random(System.currentTimeMillis());

  public static final String BLUE = "BLUE";
  public static final String BROWN = "BROWN";
  public static final String BLACK = "BLACK";
  public static final String GOLD = "GOLD";
  public static final String HAZEL = "HAZEL";
  public static final String UNKNOWN = "UNKNOWN";

  public static final String MALE = "MALE";
  public static final String FEMALE = "FEMALE";

  public static final String[] EYE_COLORS = {
      BLUE,
      BROWN,
      BLACK,
      GOLD,
      HAZEL,
      UNKNOWN
  };

  public static final String[] GENDERS = {
      MALE,
      FEMALE,
      UNKNOWN
  };

  public static void main(String[] args) {
    // Generate 50 random people
    for (int aa = 0; aa < 50; aa++) {
      System.out.println(createPerson().toString());
    }
  }

  /**
   * Create a Person object using randomly generated data.
   *
   * @return The Person object that was created.
   */
  public static Person createPerson() {
    return new Person(generateRandomLastName(), generateRandomFirstName(), generateRandomAge(),
        generateRandomEyeColor(), generateRandomGender());
  }

  /**
   * Warm up the random number generator. Call it a random
   * number of times. Inception? You bet.
   */
  private static void warmupRandomNumberGenerator() {
    // Run through a few before returning the number
    int randomNumberOfWarmups = rng.nextInt(10);
    for (int aa = 0; aa < randomNumberOfWarmups; aa++) {
      rng.nextInt();
    }
  }

  /**
   * Generate and return a random number, just like
   * it says. What? You trust Javadoc more than a method
   * signature?
   *
   * @param bound
   *          The upper bound on the RNG
   *
   * @return The next int between zero (incl) and the bound (excl)
   */
  private static int generateRandomNumber(int bound) {
    warmupRandomNumberGenerator();
    return rng.nextInt(bound);
  }

  /**
   * Generate a random last name using the LAST_NAME array
   * along with a random index into the array.
   *
   * @return String - the, um, random last name
   */
  private static String generateRandomLastName() {
    String suffix = LAST_NAME[generateRandomNumber(LAST_NAME.length)];
    return suffix;
  }

  /**
   * Generate a random first name using the LAST_NAME array
   * along with a random index into the array.
   *
   * @return String - the, um, random first name
   */
  private static String generateRandomFirstName() {
    String prefix = FIRST_NAME[generateRandomNumber(FIRST_NAME.length)];
    return prefix;
  }

  /**
   * Generates a random age between 1 and AGE_MAX.
   *
   * @return
   */
  private static int generateRandomAge() {
    return generateRandomNumber(AGE_MAX) + 1;
  }

  /**
   * Generate a random Gender from the enum of the same name.
   * Like the kind you see on YouTube.
   * Okay, not like that at all.
   *
   * @return
   */
  private static String generateRandomGender() {
    int randomIndex = generateRandomNumber(GENDERS.length * 1000) / 1000;

    return GENDERS[randomIndex];
  }

  /**
   * Generate a random EyeColor from the enum of the same name.
   * Coincidence? Um, no.
   *
   * @return
   */
  private static String generateRandomEyeColor() {
    int randomIndex = generateRandomNumber(EYE_COLORS.length * 1000) / 1000;

    return EYE_COLORS[randomIndex];
  }

  private static final String[] LAST_NAME = {
      "Anon",
      "Bazog",
      "Con",
      "Daon",
      "Engan",
      "Fan",
      "Grale",
      "Hor",
      "Ix",
      "Jaxl",
      "Kath",
      "Lane",
      "Mord",
      "Naen",
      "Oon",
      "Ptal",
      "Tindale",
      "Ugzor",
      "Vahland",
      "Wragdhen",
      "Xntlh",
      "Yagnag",
      "Zhangth"
  };

  private static final String[] FIRST_NAME = {
      "Ag",
      "Bog",
      "Cain",
      "Doan",
      "Erg",
      "Fon",
      "Gor",
      "Heg",
      "In",
      "Jar",
      "Kol",
      "Lar",
      "Mog",
      "Nag",
      "Oon",
      "Ptal",
      "Quon",
      "Rag",
      "Sar",
      "Thag",
      "Uxl",
      "Verd",
      "Wrog",
      "Xlott",
      "Yogrl",
      "Zelx"
  };

}
