package com.mips.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;
    @NotNull(message = "비밀번호를 입력해주세요.")
    private String password;
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String username;
    @NotNull(message = "휴대폰번호를 입력해주세요.")
    private String phone;
}
