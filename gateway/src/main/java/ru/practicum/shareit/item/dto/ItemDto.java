package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDto {
    private Long id; // id вещи
    @NotNull
    @NotBlank
    private String name; // краткое название вещи
    @NotNull
    @NotBlank
    private String description; // описание
    @NotNull
    private Boolean available; // статус доступности
    private UserDto owner;
    private Long requestId; // id запроса вещи
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;
}
