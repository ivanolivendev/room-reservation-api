package com.ivanolivendev.reservation.mapper;

import com.ivanolivendev.reservation.dto.response.RoomResponse;
import com.ivanolivendev.reservation.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getType(),
                room.getCapacity(),
                room.getActive(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }
}
