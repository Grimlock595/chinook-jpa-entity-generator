package com.example.chinook_jpa_entity_generator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMetadataInspector implements CommandLineRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(DatabaseMetadataInspector.class);

    private final DataSource dataSource;

    public DatabaseMetadataInspector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = "public";

            logger.info("Starting database metadata inspection.");
            logger.info("Database: {}", metadata.getDatabaseProductName());
            logger.info("Database version: {}", metadata.getDatabaseProductVersion());

            try (ResultSet tables = metadata.getTables(
                    catalog, schema, "%", new String[]{"TABLE"})) {

                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");

                    logger.info("Table: {}", tableName);

                    try (ResultSet columns = metadata.getColumns(
                            catalog, schema, tableName, "%")) {

                        while (columns.next()) {
                            logger.info(
                                    "  Column: {}, SQL type: {}, Size: {}, Nullable: {}",
                                    columns.getString("COLUMN_NAME"),
                                    columns.getString("TYPE_NAME"),
                                    columns.getInt("COLUMN_SIZE"),
                                    columns.getInt("NULLABLE")
                                            != DatabaseMetaData.columnNoNulls);
                        }
                    }

                    try (ResultSet primaryKeys = metadata.getPrimaryKeys(
                            catalog, schema, tableName)) {

                        while (primaryKeys.next()) {
                            logger.info(
                                    "  Primary key: {}",
                                    primaryKeys.getString("COLUMN_NAME"));
                        }
                    }

                    try (ResultSet foreignKeys = metadata.getImportedKeys(
                            catalog, schema, tableName)) {

                        while (foreignKeys.next()) {
                            logger.info(
                                    "  Foreign key: {}.{} references {}.{}",
                                    tableName,
                                    foreignKeys.getString("FKCOLUMN_NAME"),
                                    foreignKeys.getString("PKTABLE_NAME"),
                                    foreignKeys.getString("PKCOLUMN_NAME"));
                        }
                    }
                }
            }

            logger.info("Database metadata inspection completed.");
        }
    }
}
