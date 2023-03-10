package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderById(Long ownerId, Pageable pageable);

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) " +
            " and i.available = true")
    List<Item> search(String text, Pageable pageable);

    Set<Item> findByRequesterIn(Collection<Long> requester);
}
