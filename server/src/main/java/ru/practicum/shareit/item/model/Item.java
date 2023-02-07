package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 50, nullable = false)
    String name;

    @Column(length = 1000, nullable = false)
    String description;

    @Column(name = "is_available")
    Boolean available;

    @ManyToOne
    @JoinColumn(name = "id_owner")
    User owner;

    @Column(name = "request_id")
    Long requester;

    @Transient
    Booking lastBooking;

    @Transient
    Booking nextBooking;

    @Transient
    Set<Comment> comments;
}