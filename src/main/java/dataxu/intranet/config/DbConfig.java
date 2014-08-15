package dataxu.intranet.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DbConfig {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DbConfig.class);

    private static final String PROPERTY_NAME_DATABASE_DRIVER = "datasource.connection.driver";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "datasource.connection.password";
    private static final String PROPERTY_NAME_DATABASE_URL = "datasource.connection.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "datasource.connection.username";

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_SHOWSQL = "hibernate.showsql";

    @Autowired
    protected Environment env;

    @Bean
    protected DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env
                .getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        dataSource.setUrl(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
        dataSource.setUsername(env
                .getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
        dataSource.setPassword(env
                .getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));

        LOGGER.info("use database: url {}", dataSource.getUrl());

        return dataSource;
    }

    @Bean
    protected LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setPackagesToScan("dataxu.intranet.entity");
        bean.setJpaDialect(new HibernateJpaDialect());
        bean.setDataSource(dataSource());
        bean.setJpaVendorAdapter(jpaVendorAdapter());
        return bean;
    }

    @Bean
    protected HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabasePlatform(env
                .getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
        jpaVendorAdapter.setShowSql(env.getRequiredProperty(
                PROPERTY_NAME_HIBERNATE_SHOWSQL, Boolean.class));

        LOGGER.info("{}: {}", PROPERTY_NAME_HIBERNATE_DIALECT,
                env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));

        return jpaVendorAdapter;
    }

    @Bean
    protected PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory()
                .getObject());
        return transactionManager;
    }
}
