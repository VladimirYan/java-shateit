package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class BookingShortDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
}
