package ru.hbb.Network.PacketSystem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SerializeField {

    Class list_type() default Class.class;
    Class map_key_type() default Class.class;
    Class map_val_type() default Class.class;

}
