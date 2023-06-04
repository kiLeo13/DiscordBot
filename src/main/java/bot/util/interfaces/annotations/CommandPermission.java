package bot.util.interfaces.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.dv8tion.jda.api.Permission;

/**
 *
 * <l1>REQUIRED ANNOTATION<l1/>
 * <p>
 * Tells the bot which permission a member must have in other to run the command.
 * <p>
 * Leaving this field empty means that anyone is able to run the command.
 * <p>
 * If multiple permissions are provided, the member is required to have at least one of them and NOT ALL OF THEM in other to run the command.
 * <p>
 * <b> An exception will be thrown if you register a command that does not have this annotation present. </b>
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermission {
    Permission[] permissions() default {};
}