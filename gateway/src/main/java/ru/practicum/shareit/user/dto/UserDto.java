package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private Long id; // уникальный идентификатор
    @NotNull
    @NotBlank
    private String name; // имя или логин пользователя
    @Email
    @NotNull
    @NotBlank
    private String email; // почта пользователя
}
