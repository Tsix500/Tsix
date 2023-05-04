package com.troublefixer.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserVo {
    private Long userId;

    private String account;

    private String nickName;

    private Integer role;
}
