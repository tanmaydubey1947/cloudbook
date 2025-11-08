package com.cloudbook.common.exception.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ExceptionMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private HttpStatus httpStatus;

    private String msg;
}