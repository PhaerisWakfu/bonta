package com.gitee.phaeris.bonta.config;

import com.gitee.phaeris.bonta.CDCEvent;
import com.gitee.phaeris.bonta.ChangeDataCaptureListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author wyh
 * @since 2023/8/21
 */
@Slf4j
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(DebeziumProperties.class)
public class DebeziumConfiguration {

    @Bean
    @ConditionalOnBean(io.debezium.config.Configuration.class)
    public ChangeDataCaptureListener listener(Executor taskExecutor, List<CDCEvent> events,
                                              List<io.debezium.config.Configuration> configurations) {
        return new ChangeDataCaptureListener(taskExecutor, events, configurations);
    }

    @Bean
    public CDCEvent defaultPrintEvent() {
        return (destination, key, value) -> {
            if (log.isDebugEnabled())
                log.debug("dest[{}] key[{}] change data ===>\n{}", destination, key, value);
        };
    }
}
