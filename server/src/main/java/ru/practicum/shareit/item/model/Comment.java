package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // идентификатор комментария

    private String text; // Текст комментария

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item; // Вещь, к которой относится комментарий

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author; // Автор комментария

    private LocalDateTime created; // Дата и время создания комментария
}
