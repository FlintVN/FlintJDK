package jdk.internal.reflect;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD})
public @interface CallerSensitive {

}
