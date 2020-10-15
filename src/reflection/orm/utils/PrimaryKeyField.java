package reflection.orm.utils;

import reflection.orm.annotations.PrimaryKey;

import java.lang.reflect.Field;

public class PrimaryKeyField {

    private final Field field;
    private final String name;

    public PrimaryKeyField(Field field) {
        this.field = field;
        this.name = field.getAnnotation(PrimaryKey.class).name();
    }

    public Field getField() {
        return field;
    }

    public String getName() {
//        return field.getName();
        return this.name;
    }

    public Class<?> getType() {
        return field.getType();
    }
}
