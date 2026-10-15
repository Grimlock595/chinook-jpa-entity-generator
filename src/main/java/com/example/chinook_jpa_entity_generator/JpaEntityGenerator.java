package com.example.chinook_jpa_entity_generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class JpaEntityGenerator {

    private static final Logger logger =
            LoggerFactory.getLogger(JpaEntityGenerator.class);

    private static final String SCHEMA = "public";
    private static final Path OUTPUT_DIRECTORY =
            Path.of("generated_entities");

    private final JdbcTemplate jdbcTemplate;

    public JpaEntityGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void generateEntities() throws SQLException, IOException {
        Files.createDirectories(OUTPUT_DIRECTORY);

        try (Connection connection =
                     jdbcTemplate.getDataSource().getConnection()) {

            DatabaseMetaData metadata = connection.getMetaData();
            List<String> tableNames = new ArrayList<>();

            try (ResultSet tables = metadata.getTables(
                    connection.getCatalog(),
                    SCHEMA,
                    "%",
                    new String[]{"TABLE"})) {

                while (tables.next()) {
                    tableNames.add(tables.getString("TABLE_NAME"));
                }
            }

            for (String tableName : tableNames) {
                generateEntity(metadata, tableName);
            }

            logger.info(
                    "Generated {} entity files in {}",
                    tableNames.size(),
                    OUTPUT_DIRECTORY.toAbsolutePath());
        }
    }

    private void generateEntity(
            DatabaseMetaData metadata,
            String tableName) throws SQLException, IOException {

        List<ColumnInfo> columns = new ArrayList<>();
        Set<String> primaryKeys = new HashSet<>();
        List<ForeignKeyInfo> foreignKeys = new ArrayList<>();

        try (ResultSet keys = metadata.getPrimaryKeys(
                null, SCHEMA, tableName)) {

            while (keys.next()) {
                primaryKeys.add(keys.getString("COLUMN_NAME"));
            }
        }

        try (ResultSet result = metadata.getColumns(
                null, SCHEMA, tableName, "%")) {

            while (result.next()) {
                String columnName = result.getString("COLUMN_NAME");

                boolean autoIncrement = "YES".equalsIgnoreCase(
                        result.getString("IS_AUTOINCREMENT"));

                columns.add(new ColumnInfo(
                        columnName,
                        result.getInt("DATA_TYPE"),
                        result.getString("TYPE_NAME"),
                        result.getInt("COLUMN_SIZE"),
                        result.getInt("NULLABLE")
                                == DatabaseMetaData.columnNoNulls,
                        primaryKeys.contains(columnName),
                        autoIncrement
                ));
            }
        }

        try (ResultSet importedKeys = metadata.getImportedKeys(
                null, SCHEMA, tableName)) {

            while (importedKeys.next()) {
                foreignKeys.add(new ForeignKeyInfo(
                        importedKeys.getString("FK_NAME"),
                        importedKeys.getString("FKCOLUMN_NAME"),
                        importedKeys.getString("PKTABLE_NAME"),
                        importedKeys.getString("PKCOLUMN_NAME")
                ));
            }
        }

        String className = toClassName(tableName);
        boolean hasCompositePrimaryKey = primaryKeys.size() > 1;
        String idClassName = className + "Id";

        StringBuilder source = new StringBuilder();

        source.append("package generated_entities;\n\n")
                .append("import jakarta.persistence.Column;\n")
                .append("import jakarta.persistence.Entity;\n")
                .append("import jakarta.persistence.FetchType;\n")
                .append("import jakarta.persistence.GeneratedValue;\n")
                .append("import jakarta.persistence.GenerationType;\n")
                .append("import jakarta.persistence.Id;\n")
                .append("import jakarta.persistence.IdClass;\n")
                .append("import jakarta.persistence.JoinColumn;\n")
                .append("import jakarta.persistence.ManyToOne;\n")
                .append("import jakarta.persistence.Table;\n")
                .append("import jakarta.validation.constraints.NotNull;\n")
                .append("import jakarta.validation.constraints.Size;\n\n");

        if (hasCompositePrimaryKey) {
            source.append("@IdClass(")
                    .append(idClassName)
                    .append(".class)\n");
        }

        source.append("@Entity\n")
                .append("@Table(name = \"")
                .append(tableName)
                .append("\")\n")
                .append("public class ")
                .append(className)
                .append(" {\n\n");

        // Generate scalar fields for database columns.
        for (ColumnInfo column : columns) {
            String fieldName = toFieldName(column.name());

            if (column.primaryKey()) {
                source.append("    @Id\n");

                if (column.autoIncrement()
                        && !hasCompositePrimaryKey) {
                    source.append(
                            "    @GeneratedValue(strategy = "
                                    + "GenerationType.IDENTITY)\n");
                }
            }

            source.append("    @Column(name = \"")
                    .append(column.name())
                    .append("\"");

            if (column.size() > 0
                    && isStringType(column.sqlType())) {
                source.append(", length = ")
                        .append(column.size());
            }

            source.append(", nullable = ")
                    .append(column.notNull() ? "false" : "true")
                    .append(")\n");

            if (column.notNull()) {
                source.append("    @NotNull\n");
            }

            if (isStringType(column.sqlType())
                    && column.size() > 0) {
                source.append("    @Size(max = ")
                        .append(column.size())
                        .append(")\n");
            }

            source.append("    private ")
                    .append(javaType(
                            column.sqlType(), column.typeName()))
                    .append(" ")
                    .append(fieldName)
                    .append(";\n\n");
        }

        // Generate read-only relationship mappings for foreign keys.
        for (ForeignKeyInfo foreignKey : foreignKeys) {
            String relationshipName = relationshipFieldName(
                    foreignKey.fkColumn(),
                    foreignKey.pkTable(),
                    columns,
                    foreignKeys);

            String relatedClass = toClassName(foreignKey.pkTable());

            source.append("    @ManyToOne(fetch = FetchType.LAZY)\n")
                    .append("    @JoinColumn(name = \"")
                    .append(foreignKey.fkColumn())
                    .append("\", referencedColumnName = \"")
                    .append(foreignKey.pkColumn())
                    .append("\", insertable = false, updatable = false)\n")
                    .append("    private ")
                    .append(relatedClass)
                    .append(" ")
                    .append(relationshipName)
                    .append(";\n\n");
        }

        // Generate scalar getters and setters.
        for (ColumnInfo column : columns) {
            appendGetterAndSetter(
                    source,
                    javaType(column.sqlType(), column.typeName()),
                    toFieldName(column.name()));
        }

        // Generate relationship getters and setters.
        for (ForeignKeyInfo foreignKey : foreignKeys) {
            String relationshipName = relationshipFieldName(
                    foreignKey.fkColumn(),
                    foreignKey.pkTable(),
                    columns,
                    foreignKeys);

            appendGetterAndSetter(
                    source,
                    toClassName(foreignKey.pkTable()),
                    relationshipName);
        }

        source.append("}\n");

        if (hasCompositePrimaryKey) {
            generateIdClass(idClassName, columns);
        }

        Path outputFile = OUTPUT_DIRECTORY.resolve(
                className + ".java");

        Files.writeString(outputFile, source.toString());
        logger.info("Generated {}", outputFile);
    }

    private void appendGetterAndSetter(
            StringBuilder source,
            String type,
            String fieldName) {

        String capitalized =
                Character.toUpperCase(fieldName.charAt(0))
                        + fieldName.substring(1);

        source.append("    public ")
                .append(type)
                .append(" get")
                .append(capitalized)
                .append("() {\n")
                .append("        return ")
                .append(fieldName)
                .append(";\n")
                .append("    }\n\n")
                .append("    public void set")
                .append(capitalized)
                .append("(")
                .append(type)
                .append(" ")
                .append(fieldName)
                .append(") {\n")
                .append("        this.")
                .append(fieldName)
                .append(" = ")
                .append(fieldName)
                .append(";\n")
                .append("    }\n\n");
    }

    private void generateIdClass(
            String idClassName,
            List<ColumnInfo> columns) throws IOException {

        StringBuilder source = new StringBuilder();

        source.append("package generated_entities;\n\n")
                .append("import java.io.Serializable;\n")
                .append("import java.util.Objects;\n\n")
                .append("public class ")
                .append(idClassName)
                .append(" implements Serializable {\n\n");

        for (ColumnInfo column : columns) {
            if (column.primaryKey()) {
                source.append("    private ")
                        .append(javaType(
                                column.sqlType(), column.typeName()))
                        .append(" ")
                        .append(toFieldName(column.name()))
                        .append(";\n\n");
            }
        }

        source.append("    public ")
                .append(idClassName)
                .append("() {\n")
                .append("    }\n\n");

        for (ColumnInfo column : columns) {
            if (column.primaryKey()) {
                appendGetterAndSetter(
                        source,
                        javaType(column.sqlType(), column.typeName()),
                        toFieldName(column.name()));
            }
        }

        List<String> keyFields = new ArrayList<>();

        for (ColumnInfo column : columns) {
            if (column.primaryKey()) {
                keyFields.add(toFieldName(column.name()));
            }
        }

        source.append("    @Override\n")
                .append("    public boolean equals(Object object) {\n")
                .append("        if (this == object) return true;\n")
                .append("        if (object == null || getClass() "
                        + "!= object.getClass()) return false;\n")
                .append("        ")
                .append(idClassName)
                .append(" that = (")
                .append(idClassName)
                .append(") object;\n")
                .append("        return ");

        for (int i = 0; i < keyFields.size(); i++) {
            if (i > 0) {
                source.append(" && ");
            }

            source.append("Objects.equals(")
                    .append(keyFields.get(i))
                    .append(", that.")
                    .append(keyFields.get(i))
                    .append(")");
        }

        source.append(";\n    }\n\n")
                .append("    @Override\n")
                .append("    public int hashCode() {\n")
                .append("        return Objects.hash(");

        for (int i = 0; i < keyFields.size(); i++) {
            if (i > 0) {
                source.append(", ");
            }

            source.append(keyFields.get(i));
        }

        source.append(");\n    }\n")
                .append("}\n");

        Path outputFile = OUTPUT_DIRECTORY.resolve(
                idClassName + ".java");

        Files.writeString(outputFile, source.toString());

        logger.info(
                "Generated composite key class {}", outputFile);
    }

    private String relationshipFieldName(
            String foreignKeyColumn,
            String referencedTable,
            List<ColumnInfo> columns,
            List<ForeignKeyInfo> foreignKeys) {

        String baseName = toFieldName(foreignKeyColumn);

        if (baseName.endsWith("Id") && baseName.length() > 2) {
            baseName = baseName.substring(
                    0, baseName.length() - 2);
        }

        if (baseName.isBlank()) {
            baseName = toFieldName(referencedTable);
        }

        Set<String> usedNames = new HashSet<>();

        for (ColumnInfo column : columns) {
            usedNames.add(toFieldName(column.name()));
        }

        // Account for relationship names already assigned to other FKs.
        for (ForeignKeyInfo existing : foreignKeys) {
            if (existing.fkColumn().equals(foreignKeyColumn)) {
                break;
            }

            usedNames.add(baseRelationshipName(existing.fkColumn()));
        }

        if (!usedNames.contains(baseName)) {
            return baseName;
        }

        String candidate = baseName + toClassName(referencedTable);
        String uniqueCandidate = candidate;
        int suffix = 2;

        while (usedNames.contains(uniqueCandidate)) {
            uniqueCandidate = candidate + suffix;
            suffix++;
        }

        return uniqueCandidate;
    }

    private String baseRelationshipName(String foreignKeyColumn) {
        String name = toFieldName(foreignKeyColumn);

        if (name.endsWith("Id") && name.length() > 2) {
            name = name.substring(0, name.length() - 2);
        }

        return name;
    }

    private String javaType(int sqlType, String typeName) {
        return switch (sqlType) {
            case Types.BIGINT -> "Long";

            case Types.INTEGER, Types.SMALLINT, Types.TINYINT ->
                    "Integer";

            case Types.NUMERIC, Types.DECIMAL ->
                    "java.math.BigDecimal";

            case Types.REAL -> "Float";

            case Types.FLOAT, Types.DOUBLE -> "Double";

            case Types.BOOLEAN, Types.BIT -> "Boolean";

            case Types.DATE -> "java.time.LocalDate";

            case Types.TIME, Types.TIME_WITH_TIMEZONE ->
                    "java.time.LocalTime";

            case Types.TIMESTAMP, Types.TIMESTAMP_WITH_TIMEZONE ->
                    "java.time.LocalDateTime";

            case Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY ->
                    "byte[]";

            default -> {
                if ("serial".equalsIgnoreCase(typeName)
                        || "int4".equalsIgnoreCase(typeName)) {
                    yield "Integer";
                }

                yield "String";
            }
        };
    }

    private boolean isStringType(int sqlType) {
        return sqlType == Types.CHAR
                || sqlType == Types.VARCHAR
                || sqlType == Types.LONGVARCHAR
                || sqlType == Types.NCHAR
                || sqlType == Types.NVARCHAR
                || sqlType == Types.LONGNVARCHAR;
    }

    private String toClassName(String tableName) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char character :
                tableName.toLowerCase(Locale.ROOT).toCharArray()) {

            if (character == '_') {
                capitalizeNext = true;
            } else {
                result.append(capitalizeNext
                        ? Character.toUpperCase(character)
                        : character);

                capitalizeNext = false;
            }
        }

        return result.toString();
    }

    private String toFieldName(String columnName) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (char character :
                columnName.toLowerCase(Locale.ROOT).toCharArray()) {

            if (character == '_') {
                capitalizeNext = true;
            } else {
                result.append(capitalizeNext
                        ? Character.toUpperCase(character)
                        : character);

                capitalizeNext = false;
            }
        }

        return result.toString();
    }

    private record ColumnInfo(
            String name,
            int sqlType,
            String typeName,
            int size,
            boolean notNull,
            boolean primaryKey,
            boolean autoIncrement) {
    }

    private record ForeignKeyInfo(
            String fkName,
            String fkColumn,
            String pkTable,
            String pkColumn) {
    }
}
