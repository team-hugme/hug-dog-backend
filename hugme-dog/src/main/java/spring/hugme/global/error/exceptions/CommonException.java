package spring.hugme.global.error.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import spring.hugme.global.response.ResponseCode;

@Slf4j
@Getter
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
