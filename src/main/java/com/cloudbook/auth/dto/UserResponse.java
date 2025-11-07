package com.cloudbook.auth.dto;

import com.cloudbook.common.dto.BaseResponse;
import com.cloudbook.common.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends BaseResponse {
    private UUID id;
    private String username;
    private Role role;
    private LocalDateTime createdAt;
}