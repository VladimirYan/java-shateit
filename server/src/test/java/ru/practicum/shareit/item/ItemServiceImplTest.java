package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.CustomUserNotFoundException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
public class ItemServiceImplTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    void deleteItem() {
        User user = new User();
        user.setName("name");
        user.setEmail("ab@ab.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        itemService.deleteItem(savedItem.getId(), savedUser.getId());

        Assertions.assertEquals(Optional.empty(), itemRepository.findById(item.getId()));
    }

    @Test
    void deleteItemNotFound() {
        User user = new User();
        user.setName("name");
        user.setEmail("ab@ab.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        NotFoundException exp = assertThrows(NotFoundException.class, () -> itemService.deleteItem(999L, savedUser.getId()));

        assertEquals("Вещь с id 999 не найдена", exp.getMessage());
    }

    @Test
    void deleteItemNotFoundUser() {
        User user = new User();
        user.setName("name");
        user.setEmail("ab@ab.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        NotFoundException exp = assertThrows(NotFoundException.class, () -> itemService.deleteItem(savedItem.getId(), 777L));

        assertEquals("Пользователь с id 777 не найден", exp.getMessage());
    }

    @Test
    void add() {
        User user = new User();
        user.setName("name");
        user.setEmail("abc@abc.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        ItemDto itemDto = itemMapper.toItemDto(savedItem);

        itemService.add(itemDto, savedUser.getId());

        Assertions.assertEquals("desc", savedItem.getDescription());
    }

    @Test
    void update() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("newName");
        itemDto.setDescription("newDesc");
        itemDto.setAvailable(false);

        itemService.update(
                itemMapper.toItemDto(item),
                savedUser.getId(),
                savedItem.getId()
        );

        Assertions.assertEquals("newName", itemDto.getName());
        Assertions.assertEquals("newDesc", itemDto.getDescription());
    }

    @Test
    void updateNotFoundUser() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(user);
        Item savedItem = itemRepository.save(item);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("newName");
        itemDto.setDescription("newDesc");
        itemDto.setAvailable(false);

        NotFoundException exp = assertThrows(NotFoundException.class, () -> itemService.update(
                        itemMapper.toItemDto(item),
                        999L,
                        savedItem.getId()
                )
        );

        assertEquals("Пользователь с id=999 не найден", exp.getMessage());
    }

    @Test
    void getAll() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        List<Item> result = itemRepository.getItemsByOwner(savedUser);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getItemsByOwner() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        itemRepository.save(item);

        List<ItemDto> result = itemService.getItemsByOwner(savedUser.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getItemsBySearchQuery() {
        User user = new User();
        user.setName("name");
        user.setEmail("a@a.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        itemRepository.save(item);

        List<ItemDto> result = itemService.getItemsBySearchQuery("name");
        assertFalse(result.isEmpty());
    }

    @Test
    void deleteItemsByOwnerId() {
        User user = new User();
        user.setName("name");
        user.setEmail("ab1@ab1.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        itemRepository.save(item);

        itemService.deleteItemsByOwnerId(savedUser.getId());

        List<Item> items = itemRepository
                .findAll()
                .stream()
                .filter(i -> i.getOwner().getEmail().equals("ab1@ab1.com"))
                .toList();

        assertTrue(items.isEmpty());
    }

    @Test
    void getItemById() {
        User user = new User();
        user.setName("name");
        user.setEmail("abcd@abcd.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        itemRepository.save(item);

        ItemDto result = itemService.getItemById(item.getId());
        assertNotNull(result);
    }

    @Test
    void addComment() {
        User user = new User();
        user.setName("name");
        user.setEmail("ab@ab.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.parse("2023-12-03T10:15:30"));
        booking.setEnd(LocalDateTime.parse("2024-12-03T10:15:30"));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);

        CommentDto dto = new CommentDto();
        dto.setText("text");
        dto.setItem(itemMapper.toItemDto(item));
        dto.setAuthor(userMapper.toUserDto(user));

        assertNotNull(itemService.addComment(dto, savedItem.getId(), savedUser.getId()));
    }

    @Test
    void addCommentNotFoundUser() {
        User user = new User();
        user.setName("name");
        user.setEmail("abcde@abcde.com");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Status.CANCELED);
        booking.setBooker(savedUser);
        booking.setItem(savedItem);
        bookingRepository.save(booking);

        Comment comment = new Comment();
        comment.setText("Test");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.of(2024, 12, 23, 10, 0));

        CustomUserNotFoundException exp = assertThrows(
                CustomUserNotFoundException.class,
                () -> itemService.addComment(
                        itemMapper.toCommentDto(comment),
                        savedItem.getId(),
                        999L
                )
        );

        assertEquals("Пользователь не найден", exp.getMessage());
    }

}
