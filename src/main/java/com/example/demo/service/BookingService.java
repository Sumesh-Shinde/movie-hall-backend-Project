package com.example.demo.service;

import com.example.demo.model.Booking;
import com.example.demo.repository.BookingRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }


    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Integer> getBookedSeatsForShowtime(Long showtimeId) {
        List<Booking> bookings = bookingRepository.findByShowtimeId(showtimeId);

        return bookings.stream()
                .flatMap(booking -> 
                    booking.getSelectedSeats() != null && !booking.getSelectedSeats().isEmpty()
                        ? Arrays.stream(booking.getSelectedSeats().split(",")).map(Integer::parseInt)
                        : Arrays.stream(new Integer[0]) // If no seats booked, return empty stream
                )
                .collect(Collectors.toList());
    }

    public Booking createBooking(Booking booking) {
        booking.setBookingTime(LocalDateTime.now());

        if (booking.getSelectedSeats() == null || booking.getSelectedSeats().isEmpty()) {
            throw new RuntimeException("No seats selected!");
        }

        // ✅ Check if seats are already booked
        List<Integer> alreadyBookedSeats = getBookedSeatsForShowtime(booking.getShowtime().getId());
        List<Integer> requestedSeats = Arrays.stream(booking.getSelectedSeats().split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        for (Integer seat : requestedSeats) {
            if (alreadyBookedSeats.contains(seat)) {
                throw new RuntimeException("Seat " + seat + " is already booked!");
            }
        }

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Long id, Booking updatedBooking) {
        return bookingRepository.findById(id).map(existingBooking -> {
            existingBooking.setUser(updatedBooking.getUser());
            existingBooking.setShowtime(updatedBooking.getShowtime());
            existingBooking.setNumberOfSeats(updatedBooking.getNumberOfSeats());
            existingBooking.setTotalPrice(updatedBooking.getTotalPrice());
            existingBooking.setSelectedSeats(updatedBooking.getSelectedSeats());
            return bookingRepository.save(existingBooking);
        }).orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}
