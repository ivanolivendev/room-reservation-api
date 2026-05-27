CREATE TABLE rooms (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    type VARCHAR(40) NOT NULL,
    capacity INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE reservations (
    id UUID PRIMARY KEY,
    reservation_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    responsible_name VARCHAR(120) NOT NULL,
    status VARCHAR(40) NOT NULL,
    room_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_reservations_room FOREIGN KEY (room_id) REFERENCES rooms (id)
);

CREATE INDEX idx_reservations_room_date_status
    ON reservations (room_id, reservation_date, status);
