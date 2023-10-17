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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author wyh
 * @since 2023/8/21
 */
@Slf4j
@Component
public class MyCDCEvent implements CDCEvent {
    @Override
    public void listen(ChangeData data) {
        log.info("table ==>{}", data.getSource().getDb() + "." + data.getSource().getTable());
        log.info("operation type ==>{}", data.getOp());
        log.info("plan name before ==>{}", data.getBefore().get("name"));
        log.info("plan name after ==>{}", data.getAfter().get("name"));
    }
}
```