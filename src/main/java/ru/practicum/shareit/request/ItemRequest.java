package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequest {
    private Long itemRequestId;
    private String description;
    private Long requestorId;
    private LocalDateTime created;
}
