package bot.internal.abstractions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * This decides whether the message that triggered the command should be deleted or not.
 * <p>
 * Setting this to <b>true</b> means that the message has to be deleted automatically once the command is called.
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageDeletion {
    boolean value() default true;
}