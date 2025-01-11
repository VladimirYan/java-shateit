package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId")
    List<Booking> findByBookerId(@Param("bookerId") Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start < :start AND b.end > :end")
    List<Booking> findActiveBookings(@Param("bookerId") Long bookerId,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end < :end")
    List<Booking> findPastBookings(@Param("bookerId") Long bookerId,
                                   @Param("end") LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start > :start")
    List<Booking> findFutureBookings(@Param("bookerId") Long bookerId,
                                     @Param("start") LocalDateTime start, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.status = :status")
    List<Booking> findByBookerAndStatus(@Param("bookerId") Long bookerId,
                                        @Param("status") Status status, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId")
    List<Booking> findByOwner(@Param("ownerId") Long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start < :start AND b.end > :end")
    List<Booking> findOwnerActiveBookings(@Param("ownerId") Long ownerId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < :end")
    List<Booking> findOwnerPastBookings(@Param("ownerId") Long ownerId,
                                        @Param("end") LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > :start")
    List<Booking> findOwnerFutureBookings(@Param("ownerId") Long ownerId,
                                          @Param("start") LocalDateTime start, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status")
    List<Booking> findOwnerBookingsByStatus(@Param("ownerId") Long ownerId,
                                            @Param("status") Status status, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.end < :end ORDER BY b.end DESC")
    Booking findLastBookingBeforeEnd(@Param("itemId") Long itemId,
                                     @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > :end ORDER BY b.start ASC")
    Booking findNextBookingAfterEnd(@Param("itemId") Long itemId,
                                    @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :userId AND b.end < :end AND b.status = :status")
    Booking findLastBookingByUserAndStatus(@Param("itemId") Long itemId,
                                           @Param("userId") Long userId,
                                           @Param("end") LocalDateTime end,
                                           @Param("status") Status status);
}
