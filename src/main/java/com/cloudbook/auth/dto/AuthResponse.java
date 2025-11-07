package com.cloudbook.auth.dto;

import com.cloudbook.common.dto.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse extends BaseResponse {

    private String msg;
    private String token;
    private int expiresIn;
    private String refreshToken;
}
