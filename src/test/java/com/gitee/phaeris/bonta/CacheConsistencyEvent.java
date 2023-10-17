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
        Envelope.Operation operation = Envelope.Operation.forCode(value.getOp());
        switch (operation) {
            case UPDATE:
            case DELETE:
                if (!pass(destination)) {
                    break;
                }
                //监听到数据库变更处理缓存一致性
                log.info("删除{}相关缓存", key);
                break;
            case CREATE:
                log.info("{} ==> {}", key, operation);
                break;
        }
    }

    private static boolean pass(String destination) {
        return CAPTURE_DEST.stream()
                .anyMatch(x -> Objects.equals(x, destination));
    }
}
