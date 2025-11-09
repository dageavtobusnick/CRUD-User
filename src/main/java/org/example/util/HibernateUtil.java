package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.example.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);

    private static SessionFactory sessionFactory;

    static {
        try {
            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml")
                    .build();

            MetadataSources metadataSources = new MetadataSources(standardRegistry);
            metadataSources.addAnnotatedClass(User.class);

            Metadata metadata = metadataSources.getMetadataBuilder().build();

            sessionFactory = metadata.getSessionFactoryBuilder().build();

            logger.info("SessionFactory created successfully");

        } catch (Exception e) {
            logger.error("Failed to create SessionFactory", e);
            System.err.println("Failed to initialize Hibernate: " + e.getMessage());
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                configuration.configure();

                String url = System.getProperty("hibernate.connection.url");
                String username = System.getProperty("hibernate.connection.username");
                String password = System.getProperty("hibernate.connection.password");

                if (url != null) {
                    configuration.setProperty("hibernate.connection.url", url);
                }
                if (username != null) {
                    configuration.setProperty("hibernate.connection.username", username);
                }
                if (password != null) {
                    configuration.setProperty("hibernate.connection.password", password);
                }

                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.info("SessionFactory closed");
        }
    }

    public static boolean isInitialized() {
        return sessionFactory != null;
    }

    public static void reconfigure() {
        shutdown();
        sessionFactory = null;
    }
}
