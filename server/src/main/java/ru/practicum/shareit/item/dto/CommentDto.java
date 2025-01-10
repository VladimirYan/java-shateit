package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDto {
    private Long id; // идентификатор комментария
    private String text; // Текст комментария
    private String authorName;
    private ItemDto item; // Вещь, к которой относится комментарий
    private UserDto author; // Автор комментария
    private LocalDateTime created; // Дата и время создания комментария
}
