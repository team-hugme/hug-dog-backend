package spring.hugme.domain.auth.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequestDto {

    @Getter
    @Setter
    public static class SignUp {

        private UUID id;
        private String userId;
        private String password;
        private String email;
        private String name;
        private String phone;
        private LocalDate birthday;
        private boolean active;
    }
}


