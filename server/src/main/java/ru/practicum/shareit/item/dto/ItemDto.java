package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDto {
    private Long id; // id вещи
    private String name; // краткое название вещи
    private String description; // описание
    private Boolean available; // статус доступности
    private UserDto owner;
    private Long requestId; // id запроса вещи
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;
}
