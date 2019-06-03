package com.fundwit.sys.shikra.database;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MysqlDatabaseManagerImpl implements DatabaseManager{
    private static final String KEY_DEFAULT_CREATE_DATABASE_STATEMENT = "DEFAULT_CREATE_DATABASE_STATEMENT";
    private static final String KEY_DEFAULT_CREATE_USER_STATEMENT = "DEFAULT_CREATE_USER_STATEMENT";
    private static final String KEY_DEFAULT_GRANT_STATEMENT= "DEFAULT_GRANT_STATEMENT";

    private static final Map<String, String> sqlMap;
    static {
        sqlMap = new HashMap<>();
        sqlMap.put(KEY_DEFAULT_CREATE_DATABASE_STATEMENT+"_MYSQL",
                "CREATE SCHEMA IF NOT EXISTS %s DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
        sqlMap.put(KEY_DEFAULT_CREATE_USER_STATEMENT+"_MYSQL",
                "CREATE USER IF NOT EXISTS '%s'@'%%' IDENTIFIED BY '%s';");
        sqlMap.put(KEY_DEFAULT_GRANT_STATEMENT+"_MYSQL",
                "GRANT ALL ON %s.* TO '%s'@'%%'; FLUSH PRIVILEGES;");

        sqlMap.put(KEY_DEFAULT_CREATE_DATABASE_STATEMENT+"_H2",
                "CREATE SCHEMA IF NOT EXISTS %s ;");
        sqlMap.put(KEY_DEFAULT_CREATE_USER_STATEMENT+"_H2",
                "CREATE USER IF NOT EXISTS %s PASSWORD '%s';");
        sqlMap.put(KEY_DEFAULT_GRANT_STATEMENT+"_H2",
                "GRANT ALL ON SCHEMA %s TO %s;");
    }


    @Override
    public void initializeDatabase(DataSourceProperties properties) {
        String url = properties.getUrl();
        String databaseName;
        String baseUrl;
        String jdbcBaseUrl;
        String databaseType = url.substring(5, url.indexOf(':',6)).toUpperCase();
        if(url.startsWith("jdbc:mysql:")) {
            int idx1 = url.lastIndexOf("/");
            int idx2 = url.indexOf("?");
            if(idx2==-1){
                idx2 = url.length();
            }
            databaseName = url.substring(idx1+1, idx2);
            baseUrl = url.substring(0, idx1);
            String parameter = url.substring(idx2); // include ? prefix
            jdbcBaseUrl =  baseUrl+"/"+parameter;
        }
        else if(url.startsWith("jdbc:h2:mem:")) {
            int idx1 = url.lastIndexOf(":");
            int idx2 = url.indexOf(";");
            if(idx2==-1) {
                idx2 = url.length();
            }

            databaseName = url.substring(idx1+1, idx2);
            // baseUrl = url.substring(0, idx1);
            // String parameter = url.substring(idx2); // include ? prefix
            jdbcBaseUrl =  url;
        }else{
            throw new RuntimeException("not supported jdbc url: "+url);
        }

        Connection conn = null;
        try {
            String username = !StringUtils.isEmpty(properties.getSchemaUsername()) ?
                    properties.getSchemaUsername() : properties.getUsername();
            String password = !StringUtils.isEmpty(properties.getSchemaPassword()) ?
                    properties.getSchemaPassword() : properties.getPassword();

            DriverManager.setLoginTimeout(2);
            conn = DriverManager.getConnection(jdbcBaseUrl, username, password);
            if(conn == null) {
                throw new RuntimeException("failed to get database connection for initialize");
            }
            this.execute(String.format(sqlMap.get(KEY_DEFAULT_CREATE_DATABASE_STATEMENT+"_"+databaseType), databaseName), conn);
            if(properties.getUsername()!=null && properties.getSchemaUsername()!=null && !properties.getUsername().equals(properties.getSchemaUsername())) {
                this.execute(String.format(sqlMap.get(KEY_DEFAULT_CREATE_USER_STATEMENT+"_"+databaseType), properties.getUsername(), properties.getPassword()), conn);
                this.execute(String.format(sqlMap.get(KEY_DEFAULT_GRANT_STATEMENT+"_"+databaseType), databaseName, properties.getUsername()), conn);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if(conn!=null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

//    @Override
//    public void execute(String statement, DataSource dataSource) {
//        DatabaseUtils.execute(statement, dataSource);
//    }

    @Override
    public void execute(String statement, Connection dataSource) {
        DatabaseUtils.execute(statement, dataSource);
    }
}
