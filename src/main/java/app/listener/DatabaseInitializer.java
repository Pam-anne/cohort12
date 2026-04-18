package app.listener;

import app.database.DatabaseConnection;
import app.database.SchemaManager;
import app.model.Person;
import app.model.School;
import app.model.Trainer;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

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

    /** Register every @Cohort12Form/@Cohort12Table entity here. */
    private static final List<Class<?>> ENTITIES = Arrays.asList(
            Person.class,
            School.class,
            Trainer.class
    );

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            System.out.println("[DatabaseInitializer] Bootstrapping database layer...");

            SchemaManager.ensureDatabaseExists();
            DatabaseConnection.init();

            for (Class<?> entity : ENTITIES) {
                SchemaManager.ensureTable(entity);
            }

            System.out.println("[DatabaseInitializer] Database layer ready.");
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Startup failed: " + e.getMessage());
            throw new RuntimeException("Database bootstrap failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseConnection.close();
    }
}
