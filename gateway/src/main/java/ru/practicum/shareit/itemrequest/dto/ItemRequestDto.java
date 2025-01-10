package ru.practicum.shareit.itemrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestorId;
    private LocalDateTime created;
}
