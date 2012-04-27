package io.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Modifiable {
    
    public String classification();
    public String type();
    
}