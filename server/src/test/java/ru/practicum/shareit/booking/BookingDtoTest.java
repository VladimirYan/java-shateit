package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoTest {
    private final JacksonTester<BookingDto> json;

    @Test
    void testBookingSerialize() throws Exception {

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

        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                Status.APPROVED,
                userDto,
                itemDto
        );

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.start")
                .hasJsonPath("$.end")
                .hasJsonPath("$.status")
                .hasJsonPath("$.booker.id")
                .hasJsonPath("$.booker.name")
                .hasJsonPath("$.item.id")
                .hasJsonPath("$.item.name");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(item_id -> assertThat(item_id.longValue()).isEqualTo(bookingDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.start")
                .satisfies(created -> assertThat(created).isNotNull());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .satisfies(created -> assertThat(created).isNotNull());
        assertThat(result).extractingJsonPathStringValue("$.status")
                .satisfies(status -> assertThat(Status.valueOf(status)).isEqualTo(bookingDto.getStatus()));

        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .satisfies(booker_id -> assertThat(booker_id.longValue()).isEqualTo(bookingDto.getBooker().getId()));
        assertThat(result).extractingJsonPathStringValue("$.booker.name")
                .satisfies(booker_name -> assertThat(booker_name).isEqualTo(bookingDto.getBooker().getName()));

        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(bookingDto.getItem().getId()));
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .satisfies(item_name -> assertThat(item_name).isEqualTo(bookingDto.getItem().getName()));
    }
}
