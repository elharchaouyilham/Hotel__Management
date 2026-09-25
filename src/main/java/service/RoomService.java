package service;

import model.Room;
import model.User;
import model.enums.UserRole;
import repository.RoomRepository;

import java.util.List;
import java.util.UUID;

public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public void createRoom(User currentUser, Room room) {

        checkAdmin(currentUser);

        roomRepository.save(room);
    }

    public List<Room> getAllRoom(User currentUser) {

        checkAdmin(currentUser);

        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {

        return roomRepository.findAvailableRooms();
    }

    public Room getRoomById(User currentUser, UUID id) {

        checkAdmin(currentUser);

        return roomRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Chambre introuvable."
                        )
                );
    }

    public void updateRoom(User currentUser, Room room) {

        checkAdmin(currentUser);

        roomRepository.update(room);
    }

    public void deleteRoom(User currentUser, UUID id) {

        checkAdmin(currentUser);

        roomRepository.delete(id);
    }

    private void checkAdmin(User currentUser) {

        if (currentUser == null) {

            throw new IllegalArgumentException(
                    "Utilisateur non connecté."
            );
        }

        if (currentUser.getTitle() != UserRole.ADMIN) {

            throw new IllegalArgumentException(
                    "Accès refusé. Seul un administrateur peut effectuer cette action."
            );
        }
    }
}