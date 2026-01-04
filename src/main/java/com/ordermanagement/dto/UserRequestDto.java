package com.ordermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

    @NotBlank
    @Size(min=4,max=16)
    private String username;

    @NotBlank
   @Size(min=6)
    private String password;

}
