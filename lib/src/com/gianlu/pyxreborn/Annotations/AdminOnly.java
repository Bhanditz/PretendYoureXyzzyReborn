package com.gianlu.pyxreborn.Annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(value = {ElementType.METHOD})
public @interface AdminOnly {
}
