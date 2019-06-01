package com.fundwit.sys.shikra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.UncategorizedScriptException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.*;

public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final String DEFAULT_CREATE_DATABASE_STATEMENT =
            "CREATE SCHEMA IF NOT EXISTS %s DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;";
    private static final String DEFAULT_CREATE_USER_STATEMENT =
            "CREATE USER IF NOT EXISTS '%s'@'%%' IDENTIFIED BY '%s';";
    private static final String DEFAULT_GRANT_STATEMENT =
            "GRANT ALL ON %s.* TO '%s'@'%%'; FLUSH PRIVILEGES;";

    public static void initializeDatabase(DataSourceProperties properties) {
        String url = properties.getUrl();
        int idx1 = url.lastIndexOf("/");
        int idx2 = url.indexOf("?");
        String databaseName = url.substring(idx1+1, idx2);
        String baseUrl = url.substring(0, idx1);
        String parameter = url.substring(idx2); // include ? prefix

        String jdbcBaseUrl =  baseUrl+"/"+parameter;
        Connection conn = null;
        try {
            String username = !StringUtils.isEmpty(properties.getSchemaUsername()) ?
                    properties.getSchemaUsername() : properties.getUsername();
            String password = !StringUtils.isEmpty(properties.getSchemaUsername()) ?
                    properties.getSchemaPassword() : properties.getPassword();

            conn = DriverManager.getConnection(jdbcBaseUrl, username, password);
            DatabaseUtils.execute(String.format(DEFAULT_CREATE_DATABASE_STATEMENT, databaseName), conn);

            if(properties.getUsername()!=null && properties.getSchemaUsername()!=null && !properties.getUsername().equals(properties.getSchemaUsername())) {
                DatabaseUtils.execute(String.format(DEFAULT_CREATE_USER_STATEMENT, properties.getUsername(), properties.getPassword()), conn);
                DatabaseUtils.execute(String.format(DEFAULT_GRANT_STATEMENT, databaseName, properties.getUsername()), conn);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void execute(String statement, Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            stmt.execute(statement);
            int rowsAffected = stmt.getUpdateCount();
            SQLWarning warningToLog = stmt.getWarnings();
            while (warningToLog != null) {
                logger.warn("SQLWarning ignored: SQL state '" + warningToLog.getSQLState() +
                        "', error code '" + warningToLog.getErrorCode() +
                        "', message [" + warningToLog.getMessage() + "]");
                warningToLog = warningToLog.getNextWarning();
            }

        }finally {
            try {
                stmt.close();
            }
            catch (Throwable ex) {
                logger.trace("Could not close JDBC Statement", ex);
            }
        }
    }
    public static void execute(String statement, DataSource dataSource) throws DataAccessException {
        Assert.notNull(dataSource, "DataSource must not be null");
        try {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            try {
                execute(statement, connection);
            }
            finally {
                DataSourceUtils.releaseConnection(connection, dataSource);
            }
        }
        catch (Throwable ex) {
            if (ex instanceof ScriptException) {
                throw (ScriptException) ex;
            }
            throw new UncategorizedScriptException("Failed to execute database script", ex);
        }
    }
}
