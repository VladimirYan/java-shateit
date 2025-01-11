package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoTest {
    private final JacksonTester<ItemDto> json;

    @Test
    void testItemSerialize() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("TestName");
        userDto.setEmail("test@email.ru");

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("TestNamt")
                .description("TestDescription")
                .available(true)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.description")
                .hasJsonPath("$.available")
                .hasJsonPath("$.name");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(itemDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.name")
                .satisfies(item_name -> assertThat(item_name).isEqualTo(itemDto.getName()));
        assertThat(result).extractingJsonPathStringValue("$.description")
                .satisfies(item_description -> assertThat(item_description).isEqualTo(itemDto.getDescription()));
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .satisfies(item_available -> assertThat(item_available).isEqualTo(itemDto.getAvailable()));
    }
}