package spring.hugme.domain.auth.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReissueRequest {

    private String userId;
    private String refreshToken;
}
