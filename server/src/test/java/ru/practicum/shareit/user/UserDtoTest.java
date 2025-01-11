package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoTest {
    private final JacksonTester<UserDto> json;

    @Test
    void testUserSerialize() throws Exception {

        UserDto userDto = new UserDto(
                1L,
                "TestName",
                "test@email.ru"
        );


        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.name")
                .hasJsonPath("$.email");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(item_id -> assertThat(item_id.longValue()).isEqualTo(userDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.name")
                .satisfies(created -> assertThat(created).isNotNull());
        assertThat(result).extractingJsonPathStringValue("$.email")
                .satisfies(created -> assertThat(created).isNotNull());
    }
}
