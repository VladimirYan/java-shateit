package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentDtoTest {
    private final JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {

        UserDto userDto = new UserDto(
                1L,
                "TestName",
                "test@email.ru"
        );

        ItemDto itemDto = new ItemDto(
                1L,
                "TestName",
                "TestDescription",
                true,
                userDto,
                1L,
                null,
                null,
                null
        );

        CommentDto commentDto = new CommentDto(
                1L,
                "Text",
                "AuthorName",
                itemDto,
                userDto,
                LocalDateTime.now()
        );

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.created")
                .hasJsonPath("$.authorName")
                .hasJsonPath("$.text");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(item_id -> assertThat(item_id.longValue()).isEqualTo(commentDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .satisfies(item_name -> assertThat(item_name).isEqualTo(commentDto.getAuthorName()));
        assertThat(result).extractingJsonPathStringValue("$.text")
                .satisfies(item_description -> assertThat(item_description).isEqualTo(commentDto.getText()));
        assertThat(result).extractingJsonPathStringValue("$.created")
                .satisfies(created -> assertThat(created).isNotNull());
    }
}
