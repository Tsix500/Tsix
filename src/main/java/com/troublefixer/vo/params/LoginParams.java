package com.troublefixer.vo.params;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginParams {

    private String account;

    private String password;

    private String nickName;
}
