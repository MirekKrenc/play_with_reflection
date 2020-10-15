package reflection.orm.entity;

import reflection.orm.Person;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface EntityManager<T> {

    public static  <T> EntityManager<T> of(Class<T> clazz) {
        return new H2EntityManager<>();
    }

    public void persist(T t);

    public T find(Class<T> clazz, Object primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    public Object findById(int id, Class<T> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException;
}
