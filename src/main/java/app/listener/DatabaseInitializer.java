package app.listener;

import app.dao.BaseDAO;
import app.database.DatabaseConnection;
import app.database.SchemaManager;
import app.model.Person;
import app.model.School;
import app.model.Trainer;
import app.model.User;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;
import java.util.List;

/**
 * Runs once on application startup:
 *   1. Creates the application database if it does not exist.
 *   2. Opens the singleton JDBC connection.
 *   3. Creates a table for every registered entity class if it does not exist.
 *
 * On shutdown, closes the singleton connection.
 */
@WebListener
public class DatabaseInitializer implements ServletContextListener {

    /** Register every persisted entity here. */
    private static final List<Class<?>> ENTITIES = Arrays.asList(
            Person.class,
            School.class,
            Trainer.class,
            User.class
    );

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "12345";
    private static final String DEFAULT_ADMIN_FULL_NAME = "Mike Bavon";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            System.out.println("[DatabaseInitializer] Bootstrapping database layer...");

            SchemaManager.ensureDatabaseExists();
            DatabaseConnection.init();

            for (Class<?> entity : ENTITIES) {
                SchemaManager.ensureTable(entity);
            }

            seedDefaultAdmin();

            System.out.println("[DatabaseInitializer] Database layer ready.");
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Startup failed: " + e.getMessage());
            throw new RuntimeException("Database bootstrap failed", e);
        }
    }

    private void seedDefaultAdmin() {
        BaseDAO<User> userDao = new BaseDAO<>(User.class);
        if (userDao.findBy("username", DEFAULT_ADMIN_USERNAME) != null) {
            return;
        }
        User admin = new User(
                DEFAULT_ADMIN_USERNAME,
                BCrypt.hashpw(DEFAULT_ADMIN_PASSWORD, BCrypt.gensalt()),
                DEFAULT_ADMIN_FULL_NAME);
        userDao.insert(admin);
        System.out.println("[DatabaseInitializer] Seeded default admin user.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseConnection.close();
    }
}
