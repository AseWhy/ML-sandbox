package ru.astecom.support;

import org.apache.log4j.PropertyConfigurator;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * Конфигуратор приложения
 */
public class ApplicationConfigure {

    /**
     * Конфигурирует общие настройки приложения
     */
    public static void configure() {
        var properties = new Properties();
        properties.setProperty("log4j.rootLogger", "INFO,FILE,stdout");
        properties.setProperty("log4j.rootCategory", "INFO");
        properties.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        properties.setProperty("log4j.appender.stdout.layout",  "org.apache.log4j.PatternLayout");
        properties.setProperty("log4j.appender.stdout.layout.ConversionPattern","%d [%t] %-5p %c - %m%n");
        properties.setProperty("log4j.appender.FILE", "org.apache.log4j.RollingFileAppender");
        properties.setProperty("log4j.appender.FILE.File", "logs/" + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ZonedDateTime.now()) + ".log");
        properties.setProperty("log4j.appender.FILE.MaxFileSize", "100KB");
        properties.setProperty("log4j.appender.FILE.MaxBackupIndex", "1");
        properties.setProperty("log4j.appender.FILE.layout", "org.apache.log4j.PatternLayout");
        properties.setProperty("log4j.appender.FILE.layout.ConversionPattern", "%d [%t] %-5p %c - %m%n");
        PropertyConfigurator.configure(properties);
    }
}
