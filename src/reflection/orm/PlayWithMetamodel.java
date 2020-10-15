package reflection.orm;

import reflection.orm.utils.ColumnField;
import reflection.orm.utils.Metamodel;
import reflection.orm.utils.PrimaryKeyField;

import java.util.Arrays;
import java.util.List;

public class PlayWithMetamodel {

    public static void main(String[] args) throws NoSuchFieldException {

        Metamodel<Person> metamodel = Metamodel.of(Person.class);
        PrimaryKeyField primaryKeyField = metamodel.getPrimaryKey();
        System.out.println("Primary key : " + primaryKeyField.getName() + ", " + primaryKeyField.getType().getSimpleName());

        List<ColumnField> columnFiledList = metamodel.getColumns();
        for (ColumnField c: columnFiledList) {
            System.out.println("Column: " + c.getName() + ", " + c.getType().getSimpleName());
        }
    }
}
