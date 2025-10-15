package spring.hugme.model.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

public class UserRequestDto {

    @Getter @Setter
    public static class SignUp {
        private Long id;
        private String userId;
        private String password;
        private String email;
        private String name;
        private String phone;
        private LocalDate birthday;
        private boolean isActive;
    }

}

