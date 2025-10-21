package spring.hugme.infra.error.exceptions;

import com.honlife.core.infra.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonException extends RuntimeException {
    
    private final ResponseCode code;
    
    public CommonException(ResponseCode code) {
        this.code = code;
    }
    
    public CommonException(ResponseCode code, Exception e) {
        this.code = code;
        log.error(e.getMessage(), e);
    }
    
    public ResponseCode code() {
        return code;
    }
}
