package com.gis.sucursales.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApiResponse<T> {

    private List<String> errors = new ArrayList<>();
    private T response;

    public ApiResponse() {
    }

    public ApiResponse(T response) {
        this.response = response;
    }
    public ApiResponse(T response, List<String> errors) {
        this(response);
        this.errors.addAll(errors);
    }

    public ApiResponse(List<String> errors) {
        this(null, errors);
    }


    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }


    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

}
