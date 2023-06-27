package com.example.estore.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponseDTO<T> {
    public enum Status {
        OK, ERROR
    }
    @Builder.Default
    private Status status = Status.OK;
    private String errorMessage;
    private T body;
}
