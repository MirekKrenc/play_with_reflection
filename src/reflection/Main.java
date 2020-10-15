package reflection;

//import reflection.Person;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Class<?> clazz = "Hello".getClass();
        Class<? extends String> clazzString = "Hello".getClass();
        Class<? extends Object> clazzObject = "Hello".getClass();

        Class<?> Stringclazz = String.class;

        String nameClass = "java.lang.String";
        try {
            Class<?> clazzFromName = Class.forName(nameClass);
        } catch (ClassNotFoundException e) {

        }

        Person person = new Person("Adam");
        Class<?> personClass = person.getClass();
        Field field = null;
        try {
            field = personClass.getField("name");

        } catch (NoSuchFieldException e) {
            System.out.println("No public field 'name' in class");
        }

        Field[] fieldsPublic = personClass.getFields();
        System.out.println("getFields: " + Arrays.toString(fieldsPublic));

        Field[] fields = personClass.getDeclaredFields();
        System.out.println("getDeclaredFields: " + Arrays.toString(fields));

        try {
            Method method = personClass.getMethod("getName");
            System.out.println("getMethod: " + method.getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Method[] methods = personClass.getDeclaredMethods();
        for (Method m: methods)
            System.out.println("Method: " + m.getName());

        Constructor[] constructors = personClass.getDeclaredConstructors();
        for (Constructor c: constructors) {
            System.out.println("Constructor: " + c.getName());
            int modifiers = c.getModifiers();
            System.out.println("Modifier: " + modifiers);
            System.out.println("Binary: " + Integer.toBinaryString(modifiers));
            System.out.println("mask binary : " + Integer.toBinaryString(modifiers & 0b11111111));

            if (Modifier.isPublic(modifiers)) {
                System.out.println("PUBLIC");
            }

            if ((modifiers & 0b00000001) == 0b00000001) {
                System.out.println("public");
            }
            if ((modifiers & 0b00000010) == 0b00000010) {
                System.out.println("private");
            }
            if ((modifiers & 0b00000100) == 0b00000100) {
                System.out.println("protected");
            }
            if ((modifiers & 0b00001000) == 0b00001000) {
                System.out.println("1000");
            }
            if ((modifiers & 0b00010000) == 0b00010000) {
                System.out.println("10000");
            }
            if ((modifiers & 0b00100000) == 0b00100000) {
                System.out.println("100000");
            }
        }


    }


}

class Person extends PersonSuper{
    private String name;
    private int age;

    public Person(String name) {
        super("Poland");
        this.name = name;
    }

    private Person(int age) {
        super("PL");
    }

    protected Person() {
        super("PL");

    }

    public String getName() {
        return name;
    }
}

class PersonSuper {
    public String nationality;

    public PersonSuper(String nationality) {
        this.nationality = nationality;
    }
}
