package ru.practicum.shareit.itemrequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemrequest.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoTest {
    private final JacksonTester<ItemRequestDto> json;
    private RequestRepository requestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Test
    void testItemRequestSerialize() throws Exception {

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

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "DescriptionTest",
                1L,
                LocalDateTime.of(2024, 12, 22, 16, 0, 0),
                null
        );

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.description")
                .hasJsonPath("$.requestorId")
                .hasJsonPath("$.created")
                .hasJsonPath("$.items");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(itemRequestDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.description")
                .satisfies(itemrequest_description -> assertThat(itemrequest_description).isEqualTo(itemRequestDto.getDescription()));
        assertThat(result).extractingJsonPathNumberValue("$.requestorId")
                .satisfies(item_requestorId -> assertThat(item_requestorId.longValue()).isEqualTo(itemRequestDto.getRequestorId()));
        assertThat(result).extractingJsonPathValue("$.created")
                .satisfies(item_create -> assertThat(item_create).isNotNull());
        assertThat(result).extractingJsonPathArrayValue("$.items")
                .satisfies(items -> assertThat(items).isEqualTo(itemRequestDto.getItems()));
    }
}
