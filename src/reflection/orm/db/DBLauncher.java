package reflection.orm.db;

import org.h2.tools.Server;

import java.sql.SQLException;

public class DBLauncher {

    public static void main(String... args) throws SQLException {
        String h2TcpPort = "8888";
        //Server.createTcpServer(new String[]{"-tcp", "-tcpAllowOthers", "-tcpPort", h2TcpPort}).start();
        Server.createTcpServer(new String[]{"-tcp"}).start();
        System.out.println("H2 server started");
    }
}
