# Bonta![version](https://img.shields.io/github/v/release/PhaerisWakfu/bonta)

> SpringBoot内嵌式CDC框架

## 引入依赖
```xml
<dependency>
    <groupId>com.gitee.phaeris</groupId>
    <artifactId>bonta</artifactId>
</dependency>
<dependency>
    <groupId>io.debezium</groupId>
    <artifactId>debezium-connector-mysql</artifactId>
</dependency>
```

### 直接使用可添加yml配置
```yaml
bonta:
  datasource:
    my-connector:
      snapshot-mode: schema_only
      connector-type: mysql
      offset-backing-store-type: file
      storage-file: F:/debezium/storage_my.dat
      history-file: F:/debezium/history_my.dat
      flush-interval: 10000
      server-id: 1
      server-name: mysql-1
      hostname: 127.0.0.1
      port: 3306
      user: root
      password: root
      database-whitelist: show
# 根据需求设置cdc engine的线程池参数
# 不想使用spring的线程池可自己配置一个{@link java.util.concurrent.Executor}注册为bean
spring:
  task:
    execution:
      thread-name-prefix: scindapsus-
      pool:
        core-size: 8
        max-size: 16
        queue-capacity: 10
        keep-alive: 30s
        allow-core-thread-timeout: false
      shutdown:
        await-termination: true
        await-termination-period: 60s
logging:
  level:
    com.scindapsus.dbzm: debug

```

### 注册自己的CDCEvent
```java
package com.gitee.phaeris.bonta;

import com.google.common.collect.Lists;
import io.debezium.data.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wyh
 * @since 2023/8/21
 */
@Slf4j
@Component
public class CacheConsistencyEvent implements CDCEvent {

    private static final String SERVER_NAME = "mysql-1";

    private static final String SCHEMA_NAME = "show";

    private static final List<String> TABLES;

    private static final List<String> CAPTURE_DEST;

    static {
        TABLES = Lists.newArrayList("area", "life");
        CAPTURE_DEST = TABLES.stream()
                .map(x -> String.format("%s.%s.%s", SERVER_NAME, SCHEMA_NAME, x))
                .collect(Collectors.toList());
    }

    @Override
    public void onMessage(String destination, Map<String, Object> key, ChangeData value) {
        Envelope.Operation operation = value.getOperation();
        switch (operation) {
            case UPDATE:
            case DELETE:
                if (!pass(destination)) {
                    break;
                }
                //监听到数据库变更处理缓存一致性
                log.info("删除{}相关缓存", key);
                break;
        }
    }

    private static boolean pass(String destination) {
        return CAPTURE_DEST.stream()
                .anyMatch(x -> Objects.equals(x, destination));
    }
}
```