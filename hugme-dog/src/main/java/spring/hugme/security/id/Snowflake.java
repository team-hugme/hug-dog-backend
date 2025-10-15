package spring.hugme.security.id;

import org.springframework.stereotype.Component;

@Component
public class Snowflake {
        private final long nodeId;
        private final long epoch = System.currentTimeMillis()/1000;
        private long lastTimestamp = -1L;
        private long sequence = 0L;

        private final long nodeIdBits = 10L;
        private final long sequenceBits = 12L;
        private final long nodeIdShift = sequenceBits;
        private final long timestampShift = sequenceBits + nodeIdBits;
        private final long sequenceMask = ~(-1L << sequenceBits);
        private final long maxNodeId = ~(-1L << nodeIdBits);

        public Snowflake() {
            this.nodeId = 1L; // 서버 노드 ID (여러 서버면 다르게 설정)
            if (nodeId < 0 || nodeId > maxNodeId) {
                throw new IllegalArgumentException("NodeId out of range");
            }
        }

        public synchronized long nextId() {
            long timestamp = System.currentTimeMillis();

            if (timestamp < lastTimestamp) {
                throw new RuntimeException("Clock moved backwards.");
            }

            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) & sequenceMask;
                if (sequence == 0) {
                    while (timestamp <= lastTimestamp) {
                        timestamp = System.currentTimeMillis();
                    }
                }
            } else {
                sequence = 0;
            }

            lastTimestamp = timestamp;

            return ((timestamp - epoch) << timestampShift) | (nodeId << nodeIdShift) | sequence;
        }
    }
