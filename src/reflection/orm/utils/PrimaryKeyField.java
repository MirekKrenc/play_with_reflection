package reflection.orm.utils;

import java.lang.reflect.Field;

public class PrimaryKeyField {

    private final Field field;

    public PrimaryKeyField(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return field.getName();
    }

    public Class<?> getType() {
        return field.getType();
    }
}
