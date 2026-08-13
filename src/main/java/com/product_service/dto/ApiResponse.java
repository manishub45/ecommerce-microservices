package com.product_service.dto;

public class ApiResponse<T> {

    private String message;
    private int status;
    private  T data;

    public ApiResponse() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setData(T categoriesDto) {
        this.data = categoriesDto;
    }

    public T getData() {
        return data;
    }
}
