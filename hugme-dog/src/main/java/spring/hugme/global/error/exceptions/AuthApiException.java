package spring.hugme.global.error.exceptions;
import lombok.extern.slf4j.Slf4j;
import spring.hugme.global.response.ResponseCode;

@Slf4j
public class AuthApiException extends CommonException {

    public AuthApiException(ResponseCode code) {
        super(code);
    }
    public AuthApiException(ResponseCode code, Exception e) {
        super(code, e);
        log.error(e.getMessage(), e);
    }
}
