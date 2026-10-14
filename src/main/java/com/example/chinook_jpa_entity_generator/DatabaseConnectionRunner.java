package com.example.chinook_jpa_entity_generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionRunner implements CommandLineRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(DatabaseConnectionRunner.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseConnectionRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        String databaseName = jdbcTemplate.queryForObject(
                "SELECT current_database()", String.class);

        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables " +
                "WHERE table_schema = 'public' AND table_type = 'BASE TABLE'",
                Integer.class);

        logger.info("Database connection successful.");
        logger.info("Connected database: {}", databaseName);
        logger.info("Public tables found: {}", tableCount);
    }
}
