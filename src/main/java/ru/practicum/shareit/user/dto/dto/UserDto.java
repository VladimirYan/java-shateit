package ru.practicum.shareit.user.dto.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class UserDto {

    private Long id;
    private String name;
    @Email(message = "Неправильный адрес электронной почты")
    private String email;
}
