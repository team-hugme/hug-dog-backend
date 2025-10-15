package spring.hugme.security.id;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class Snowflake {
    private final long nodeId;
    private final long epoch;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    private static final long NODE_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long NODE_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + NODE_ID_BITS;
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    private static final long MAX_NODE_ID = ~(-1L << NODE_ID_BITS);

    public Snowflake(@Value("${snowflake.node-id:1}") long nodeId) {
        if (nodeId < 0 || nodeId > MAX_NODE_ID) {
            throw new IllegalArgumentException(
                String.format("NodeId must be between 0 and %d, but got: %d", MAX_NODE_ID, nodeId)
            );
        }

        this.nodeId = nodeId;
        this.epoch = Instant.now().toEpochMilli();
    }

    public synchronized long nextId() {
        long timestamp = getCurrentTimestamp();

        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(
                String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp)
            );
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << TIMESTAMP_SHIFT)
            | (nodeId << NODE_ID_SHIFT)
            | sequence;
    }

    private long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
}