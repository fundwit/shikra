package com.fundwit.sys.shikra.database;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.sql.Connection;

public interface DatabaseManager {
    void initializeDatabase(DataSourceProperties properties);
//    void execute(String statement, DataSource dataSource);
    void execute(String statement, Connection dataSource);
}
