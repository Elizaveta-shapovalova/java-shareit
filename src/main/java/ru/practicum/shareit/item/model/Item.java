package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "items")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(length = 255, nullable = false)
    String name;
    @Column(length = 512, nullable = false)
    String description;
    @Column(name = "is_available", nullable = false)
    Boolean available;
    @Column(name = "owner_id", nullable = false)
    Long owner;
    @Column(name = "request_id")
    Long request;
    @Transient
    Booking lastBooking;
    @Transient
    Booking nextBooking;
    @Transient
    Set<Comment> comments;
}
