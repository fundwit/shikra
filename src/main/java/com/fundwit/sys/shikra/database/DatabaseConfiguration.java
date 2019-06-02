package com.fundwit.sys.shikra.database;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Configuration
    @ConditionalOnExpression("environment['spring.datasource.url']!=null && !${application.test:false}")  // spring.datasource 已配置并且 application.test != true
    public static class NonEmbeddedDataSource {

        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.hikari")
        public DataSource dataSource(DataSourceProperties properties, DatabaseManager databaseManager) {
            databaseManager.initializeDatabase(properties);
            HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class)
                    .build();
            if (StringUtils.hasText(properties.getName())) {
                dataSource.setPoolName(properties.getName());
            }
            return dataSource;
        }
    }

    @Bean
    @ConditionalOnExpression("environment['spring.datasource.url'] == null || ${application.test:false}")
    public DataSource embeddedDataSource() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                //.addScript("schema.sql")
                .build();
    }
}