package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_Id(Long bookerId, Sort sort);

    @Query("""
           SELECT b FROM Booking b\s
           WHERE b.booker.id = :bookerId\s
             AND b.status = :waiting
           """)
    List<Booking> findAllByBookerIdAndWaitingStatus(Long bookerId, BookingStatus waiting, Sort sort);

    @Query("""
           SELECT b FROM Booking b\s
           WHERE b.booker.id = :bookerId\s
             AND b.status IN :rejected
           """)
    List<Booking> findAllByBookerIdAndRejectedStatus(Long bookerId, List<BookingStatus> rejected, Sort sort);

    @Query("""
           SELECT b FROM Booking b\s
           WHERE b.booker.id = :bookerId\s
             AND b.start < :now\s
             AND b.end > :now
           """)
    List<Booking> findAllByBookerIdAndCurrentStatus(Long bookerId, LocalDateTime now, Sort sort);

    @Query("""
           SELECT b FROM Booking b\s
           WHERE b.booker.id = :bookerId\s
             AND b.start > :now
           """)
    List<Booking> findAllByBookerIdAndFutureStatus(Long bookerId, LocalDateTime now, Sort sort);

    @Query("""
           SELECT b FROM Booking b\s
           WHERE b.booker.id = :bookerId\s
             AND b.end < :now
           """)
    List<Booking> findAllByBookerIdAndPastStatus(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItem_Owner_Id(Long ownerId);

    @Query("""
           SELECT b FROM Booking b\s
           WHERE b.item.id IN :itemsIds
           """)
    List<Booking> findAllByOwnerItems(List<Long> itemsIds, Sort sort);

    @Query("""
           SELECT b FROM Booking b\s
           WHERE b.item.id IN :itemsIds\s
             AND b.status = :waiting
           """)
    List<Booking> findAllByOwnerItemsAndWaitingStatus(List<Long> itemsIds, BookingStatus waiting, Sort sort);

    @Query("""
           SELECT b FROM Booking b\s
           WHERE b.item.id IN :itemsIds\s
             AND b.status IN :rejected
           """)
    List<Booking> findAllByOwnerItemsAndRejectedStatus(List<Long> itemsIds, List<BookingStatus> rejected, Sort sort);

    @Query("""
           SELECT b FROM Booking b\s
           WHERE b.item.id IN :itemsIds\s
             AND b.start < :now\s
             AND b.end > :now
           """)
    List<Booking> findAllByOwnerItemsAndCurrentStatus(List<Long> itemsIds, LocalDateTime now, Sort sort);

    @Query("""
           SELECT b FROM Booking b\s
           WHERE b.item.id IN :itemsIds\s
             AND b.start > :now
           """)
    List<Booking> findAllByOwnerItemsAndFutureStatus(List<Long> itemsIds, LocalDateTime now, Sort sort);

    @Query("""
           SELECT b FROM Booking b\s
           WHERE b.item.id IN :itemsIds\s
             AND b.end < :now
           """)
    List<Booking> findAllByOwnerItemsAndPastStatus(List<Long> itemsIds, LocalDateTime now, Sort sort);

    @Query("""
           SELECT b FROM Booking b\s
           WHERE b.item.id = :itemId\s
             AND b.booker.id = :bookerId\s
             AND b.end <= :now
           """)
    List<Booking> findAllByUserIdAndItemIdAndEndDateIsPassed(Long bookerId, Long itemId, LocalDateTime now);
}