package bot.commands;

import bot.util.interfaces.CommandExecutor;
import bot.util.interfaces.annotations.CommandPermission;
import com.mysql.cj.MysqlConnection;
import com.mysql.cj.jdbc.MysqlDataSource;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

@CommandPermission(permissions = Permission.ADMINISTRATOR)
public class ConnectDataBase implements CommandExecutor {

    @Override
    public void run(@NotNull Message message) {
        try {
            MysqlDataSource dataSource = new MysqlDataSource();

            dataSource.setServerName("51.222.11.221");
            dataSource.setPort(3306);
            dataSource.setDatabaseName("s4179_OficinaMyuu");
            dataSource.setUser("u4179_WUpF9EeHNI");
            dataSource.setPassword("BWKWFRzHcsHzB@js.rg@MGcN");

            MysqlConnection connection = (MysqlConnection) dataSource.getConnection();

            System.out.println("Connected!");
            System.out.println(connection.getUser());
            connection.normalClose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    
}