package reflection.orm.entity;

import reflection.orm.utils.ColumnField;
import reflection.orm.utils.Metamodel;

import javax.xml.soap.Detail;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractEntityManager<T> implements EntityManager<T> {

    private AtomicInteger idGenerator = new AtomicInteger(0);



    @Override
    public void persist(T t) {

        Metamodel<T> metamodel = Metamodel.of(t.getClass());
        String sql = metamodel.buildInsertRequest();
        
        try (PreparedStatement preparedStatement = prepareStatementWith(sql).andParameters(t);){
            preparedStatement.executeUpdate();
        } catch (SQLException | IllegalAccessException s) {
            s.printStackTrace();
        }
    }

    @Override
    public T find(Class<T> clazz, Object primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Metamodel metamodel = Metamodel.of(clazz);
        String sql = metamodel.buildSelectRequest();

        try (PreparedStatement preparedStatement = prepareStatementWith(sql).andPrimaryKey(primaryKey);
                    final ResultSet resultSet = preparedStatement.executeQuery();) {
            return buildInstance(clazz, resultSet);
        }
    }

    private T buildInstance(Class<T> clazz, ResultSet resultSet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SQLException {
        Metamodel metamodel = Metamodel.of(clazz);
        T t = clazz.getConstructor().newInstance();
        final Field primaryKeyField = metamodel.getPrimaryKey().getField();
        final String primaryKeyName = metamodel.getPrimaryKey().getName();

        resultSet.next();

        if (primaryKeyField.getType() == int.class) {
            final int primaryKey = resultSet.getInt(primaryKeyName);
            primaryKeyField.setAccessible(true);
            primaryKeyField.set(t, primaryKey);
        }

        final List<ColumnField> columns = metamodel.getColumns();
        for (ColumnField cf: columns) {
            final Field columnField = cf.getField();
            columnField.setAccessible(true);
            final Class<?> columnFieldType = columnField.getType();
            final String columnFieldName = columnField.getName();
            if (columnFieldType == int.class) {
                int v = resultSet.getInt(columnFieldName);
                columnField.set(t, v);
            } else if (columnFieldType == String.class) {
                String v = resultSet.getString(columnFieldName);
                columnField.set(t, v);
            }
        }

        return t;
    }


    private PreparedStatementWrapper prepareStatementWith(String sql)  {
        PreparedStatement preparedStatement = null;
        String urlJDBC = buildH2Connection();
        Connection connection;
        try {
            connection = DriverManager.getConnection(urlJDBC, "sa", "");

            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS person (cid int primary key,name varchar(40),age int);");
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return new PreparedStatementWrapper(preparedStatement);
    }

    public abstract String buildH2Connection();

    @Override
    public Object findById(int id, Class<T> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Metamodel<T> mm = Metamodel.of(clazz);
        String sql = "select * from " + clazz.getSimpleName() + " where cid = ?";
        T t = (T) clazz.getConstructor().newInstance();
        Object o =  null;
        String urlJDBC = "jdbc:h2:mem:testdb";

        final Field fieldId = mm.getPrimaryKey().getField();
        mm.getColumns().stream().map(c -> c.getField());
        Constructor<?> constructor = mm.getConstructor(2);

        try (Connection connection = DriverManager.getConnection(urlJDBC, "sa", "");
                 PreparedStatement preparedStatement = connection.prepareStatement(sql);){
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null) {
                while (resultSet.next()) {
                   o =  constructor.newInstance(resultSet.getString("name"),
                            resultSet.getInt("age"));
                    System.out.println(resultSet.getString("name"));
                }
            }
        } catch (SQLException | InvocationTargetException e)
        {
            e.printStackTrace();
        }

        return o;
    }

    private class PreparedStatementWrapper {

        private PreparedStatement preparedStatement;

        public PreparedStatementWrapper(PreparedStatement preparedStatement) {

            this.preparedStatement = preparedStatement;
        }

        public PreparedStatement andParameters(T t) throws SQLException, IllegalAccessException {
            Metamodel<T> metamodel = Metamodel.of(t.getClass());
            Class<?> pkType = metamodel.getPrimaryKey().getType();
            //implementation strict fo Person class as the id is declared as int
            if (pkType == int.class) {
                int id = idGenerator.incrementAndGet();
                preparedStatement.setInt(1, id); //automatic inceremted id - simple solution
                final Field field = metamodel.getPrimaryKey().getField();
                field.setAccessible(true);
                field.set(t, id);
            }
            //setting fields annotated as column
            for (int colIndex=0; colIndex < metamodel.getColumns().size(); colIndex++) {
                ColumnField columnField = metamodel.getColumns().get(colIndex);
                Class<?> columnType = columnField.getType();
                Field field = columnField.getField();
                //read field
                //1 make accessible - it is most probably private
                field.setAccessible(true);
                //2 read field
                final Object value = field.get(t);
                //2 check the type and set apprpriate
                if (columnType == int.class) {
                    preparedStatement.setInt(colIndex + 2, (int)value);
                } else if (columnType == String.class) {
                    preparedStatement.setString(colIndex + 2, (String) value);
                }
            }
            return preparedStatement;
        }

        public PreparedStatement andPrimaryKey(Object primaryKey) throws SQLException {
            if (primaryKey.getClass() == Integer.class) {
                preparedStatement.setInt(1, (Integer)primaryKey);
            }
            return preparedStatement;
        }
    }
}
