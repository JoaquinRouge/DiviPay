package com.divipay.auth.dto;

public record AuthResponseDto(String username,String message,String jwt,boolean status) {

}
