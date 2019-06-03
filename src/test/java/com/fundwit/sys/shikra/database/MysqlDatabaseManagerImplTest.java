package com.fundwit.sys.shikra.database;

import org.junit.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;


public class MysqlDatabaseManagerImplTest {

    @Test
    public void testH2(){
        MysqlDatabaseManagerImpl databaseManager = new MysqlDatabaseManagerImpl();

        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl("jdbc:h2:mem:test"+System.currentTimeMillis());
        databaseManager.initializeDatabase(properties);

        properties.setUrl("jdbc:h2:mem:test"+System.currentTimeMillis()+";MODE=MySQL");
        databaseManager.initializeDatabase(properties);
    }
    @Test(expected = Exception.class)
    public void testNonEmbeddedDataSourceNotConnectable1(){
        MysqlDatabaseManagerImpl databaseManager = new MysqlDatabaseManagerImpl();
        DataSourceProperties properties = new DataSourceProperties();

        properties.setUrl("jdbc:mysql://localhost:3307");
        databaseManager.initializeDatabase(properties);
    }

    @Test(expected = Exception.class)
    public void testNonEmbeddedDataSourceNotConnectable2(){
        MysqlDatabaseManagerImpl databaseManager = new MysqlDatabaseManagerImpl();
        DataSourceProperties properties = new DataSourceProperties();

        properties.setUrl("jdbc:mysql://localhost:3307?a=b");
        databaseManager.initializeDatabase(properties);
    }

    @Test(expected = Exception.class)
    public void testBadProvider(){
        MysqlDatabaseManagerImpl databaseManager = new MysqlDatabaseManagerImpl();
        DataSourceProperties properties = new DataSourceProperties();

        properties.setUrl("jdbc:xxx://localhost:3307?a=b");
        databaseManager.initializeDatabase(properties);
    }
}