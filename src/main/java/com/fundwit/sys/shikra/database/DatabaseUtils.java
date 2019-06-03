package com.fundwit.sys.shikra.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.UncategorizedScriptException;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(MysqlDatabaseManagerImpl.class);

    public static void execute(String statement, Connection connection) {
        try {
            Statement stmt = connection.createStatement();
            try {
                stmt.execute(statement);
                logger.info("[sql] execute statement: {}", statement);
                int rowsAffected = stmt.getUpdateCount();
                logger.info("[sql]affected rows: {}", rowsAffected);
                SQLWarning warningToLog = stmt.getWarnings();
                while (warningToLog != null) {
                    logger.warn("[sql] SQLWarning ignored: SQL state '" + warningToLog.getSQLState() +
                            "', error code '" + warningToLog.getErrorCode() +
                            "', message [" + warningToLog.getMessage() + "]");
                    warningToLog = warningToLog.getNextWarning();
                }
            } finally {
                try {
                    stmt.close();
                } catch (Throwable ex) {
                    logger.warn("[sql] Could not close JDBC Statement", ex);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
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
