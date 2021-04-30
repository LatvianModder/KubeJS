package dev.latvian.kubejs.docs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author ILIKEPIEFOO2
 * This annotation indicates an EventJS class and specifies the associated
 * event IDs.
 */
@Documented
@Target(ElementType.TYPE)
public @interface KubeJSEvent {
    String[] startup() default {};
    String[] client() default {};
    String[] server() default {};
}
