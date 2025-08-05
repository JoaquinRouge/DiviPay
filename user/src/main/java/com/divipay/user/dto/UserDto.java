package com.divipay.user.dto;

import java.time.LocalDate;

public record UserDto(Long id,String email,String fullName,LocalDate createdAt) {

}
