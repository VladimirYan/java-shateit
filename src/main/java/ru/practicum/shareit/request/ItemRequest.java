package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequest {
    private Long itemRequestId; // ид запроса
    private String description; // описание предмета
    private Long requestorId; // ид пользователя, который создал запрос
    private LocalDateTime created; // дата и время создания запроса
}
