package reflection.orm;

import reflection.orm.entity.EntityManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class WritingAndReadingObjects {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SQLException, InvocationTargetException {

        EntityManager<Person> entityManager = EntityManager.of(Person.class);

        Person anna = new Person("Anna", 30);
        Person darek = new Person("Dariusz", 45);

        entityManager.persist(darek);
        entityManager.persist(anna);

        System.out.println("Afted wriitng to db");

        System.out.println(darek);
        System.out.println(anna);

        System.out.println("reading from db");
        //entityManager.findById(0);
        final Person person = (Person) entityManager.findById(1, Person.class);
        final Person person1 = (Person) entityManager.findById(2, Person.class);

        System.out.println(person);
        System.out.println(person1);

        final Person person2 = entityManager.find(Person.class, 1);
        final Person person3 = entityManager.find(Person.class, 2);

        System.out.println("Reading from db - second method");

        System.out.println(person2);
        System.out.println(person3);


        //entityManager.findById(3);
    }
}
