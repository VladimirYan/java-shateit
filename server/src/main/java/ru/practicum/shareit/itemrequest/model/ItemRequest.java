package ru.practicum.shareit.itemrequest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long itemRequestId; // уникальный идентификатор

    private String description;

    @Column(name = "requestor_id")
    private Long requestorId;

    private LocalDateTime created;
}
