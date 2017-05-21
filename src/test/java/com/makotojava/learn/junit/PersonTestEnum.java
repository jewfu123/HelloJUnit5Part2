package com.makotojava.learn.junit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public enum PersonTestEnum {

  // Note: these must match EXACTLY with the INSERT statements in
  /// sql/insert_data.sql, including the order!
  PERSON_1(1L, "Wragdhen", "Zelx", 28, PersonGenerator.BLUE, PersonGenerator.MALE),
  PERSON_2(2L, "Jaxl", "Lar", 21, PersonGenerator.BROWN, PersonGenerator.MALE),
  PERSON_3(3L, "Kath", "Fon", 35, PersonGenerator.GOLD, PersonGenerator.FEMALE),
  PERSON_4(4L, "Yagnag", "Mog", 18, PersonGenerator.BROWN, PersonGenerator.FEMALE),
  PERSON_5(5L, "Ugzor", "Sar", 11, PersonGenerator.HAZEL, PersonGenerator.UNKNOWN);

  private Person person;

  public Person getPerson() {
    return this.person;
  }

  PersonTestEnum(Long id, String lastName, String firstName, int age, String eyeColor, String gender) {
    this.person = new Person(lastName, firstName, age, eyeColor, gender).withId(id);
  }

  public static String generateInsertSql() {
    String ret = "sql/insert_data.sql";
    try {
      File insertScriptFile = new File("./src/test/resources/sql/insert_data.sql");
      try (BufferedWriter bw = new BufferedWriter(
          new FileWriter(insertScriptFile))) {
        StringBuilder sb = new StringBuilder();

        for (PersonTestEnum testPerson : PersonTestEnum.values()) {
          sb.append("INSERT INTO ");
          sb.append(Person.TABLE_NAME);
          sb.append(" (last_name, first_name, age, eye_color, gender) VALUES(");
          sb.append("'");
          sb.append(testPerson.getPerson().getLastName());
          sb.append("', '");
          sb.append(testPerson.getPerson().getFirstName());
          sb.append("', ");
          sb.append(testPerson.getPerson().getAge());
          sb.append(", '");
          sb.append(testPerson.getPerson().getEyeColor());
          sb.append("', '");
          sb.append(testPerson.getPerson().getGender());
          sb.append("'");
          sb.append(");\n");
        }
        bw.write(sb.toString());
        bw.newLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ret;
  }

}
