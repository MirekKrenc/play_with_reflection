package reflection.orm.entity;

import java.sql.Connection;

public class H2EntityManager<T> extends AbstractEntityManager<T> {

    public  String buildH2Connection() {
        Connection connection = null;
        String urlJDBC = "jdbc:h2:tcp://localhost:8082/mem:testdb";
        urlJDBC = "jdbc:h2:mem:testdb";
        return urlJDBC;
    }

}
