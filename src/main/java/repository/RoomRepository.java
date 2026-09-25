package repository;

import model.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository {

    void save(Room room);

    Optional<Room> findById(UUID id);

    List<Room> findAll();

    List<Room> findAvailableRooms();

    void update(Room room);

    void delete(UUID id);
}