/**
 * Concrete implementation of the AbstractSpringConfiguration class
 * so we can run tests.
 * 
 * This one provides an Embedded Derby database as the DataSource
 * 
 * @author J Steven Perry
 *
 */
package com.makotojava.learn.junit5;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Tag("advanced")
/**
 * This annotation marks a class or method as an Advanced JUnit construct so
 * it can be filtered by the build.
 * 
 * @author J Steven Perry
 *
 */
public @interface Advanced {
  // Nothing to do
}
