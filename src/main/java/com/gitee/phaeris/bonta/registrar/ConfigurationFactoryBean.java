package com.gitee.phaeris.bonta.registrar;

import com.gitee.phaeris.bonta.DebeziumConfigBuilder;
import com.gitee.phaeris.bonta.config.DebeziumProperties;
import io.debezium.config.Configuration;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author wyh
 * @since 2022/11/1
 */
public class ConfigurationFactoryBean implements FactoryBean<Configuration> {

    private final String name;

    private final DebeziumProperties.DatasourceProperties properties;

    public ConfigurationFactoryBean(String name, DebeziumProperties.DatasourceProperties properties) {
        this.name = name;
        this.properties = properties;
    }

    @Override
    public Configuration getObject() {
        return DebeziumConfigBuilder.build(name, properties);
    }

    @Override
    public Class<Configuration> getObjectType() {
        return Configuration.class;
    }
}
