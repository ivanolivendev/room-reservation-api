package com.ivanolivendev.reservation.repository;

import com.ivanolivendev.reservation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    boolean existsByNameIgnoreCase(String name);

    List<Room> findByActiveTrue();
}
