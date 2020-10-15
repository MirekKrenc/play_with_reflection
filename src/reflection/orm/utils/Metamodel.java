package reflection.orm.utils;

import reflection.orm.annotations.Column;
import reflection.orm.annotations.PrimaryKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Metamodel<T> {

    private final Class<T> clazz;

    public static <T> Metamodel of(Class<T> clazz) {
        return new Metamodel(clazz);
    }

    public Metamodel(Class<T> clazz) {
        this.clazz = clazz;
    }

    public PrimaryKeyField getPrimaryKey() {
        final Field[] declaredFields = clazz.getDeclaredFields();
        final Field field = Arrays.asList(declaredFields)
                .stream()
                .filter(f -> f.getAnnotation(PrimaryKey.class) != null)
                .findFirst().get();

        return new PrimaryKeyField(field);
    }

    public List<ColumnField> getColumns() {
        final Field[] declaredFields = clazz.getDeclaredFields();
        final List<ColumnField> columnFields = Arrays.asList(declaredFields)
                .stream()
                .filter(f -> f.getAnnotation(Column.class) != null)
                .map(f -> new ColumnField(f))
                .collect(Collectors.toList());
        return columnFields;
    }

    public String buildInsertRequest() {

        //insert into Person (id, name, age) values (?, ?, ?);
        final List<String> columnsList = buildColumnNames();

        String columnPart = String.join(", ", columnsList);

        int numberOfColumns = columnsList.size();
        final String questionMarksPart = IntStream.range(0, numberOfColumns).mapToObj(index -> "?")
                .collect(Collectors.joining(","));

        return "insert into " + clazz.getSimpleName() + "(" + columnPart + ") values (" + questionMarksPart + ")" ;
    }

    private List<String> buildColumnNames() {
        String primaryKeyColumnName =  getPrimaryKey().getName();

        final List<String> columnsList = getColumns().stream().map(ColumnField::getName).collect(Collectors.toList());
        //add primary key on 1st position
        columnsList.add(0, primaryKeyColumnName);
        return columnsList;
    }


    public Constructor<?> getConstructor(int paramCount) {
        final Constructor<?> constructor = Arrays.stream(clazz.getConstructors())
                .filter(c -> c.getParameterCount() == paramCount)
                .findFirst().get();

        return constructor;
    }

    public String buildSelectRequest() {
        //select id, name, age from Person where id = ?
        final List<String> columnsList = buildColumnNames();
        String columnListSQL = String.join(", ", columnsList);
        return "select " + columnListSQL + " from " + clazz.getSimpleName() + " where " + getPrimaryKey().getName() + " = ?";
        //return "select id, name, age from " + clazz.getSimpleName() + " where " + getPrimaryKey().getName() + " = ?";
    }
}
