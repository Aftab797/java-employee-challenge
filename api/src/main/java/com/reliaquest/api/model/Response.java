package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Response<T> {
    private T data;
    private Status status;
    private String error;
}
