import config.DatabaseMigration;
import model.Reservation;
import model.Room;
import model.User;
import model.enums.RoomStatus;
import model.enums.RoomType;
import model.enums.UserRole;
import repository.ReservationRepository;
import repository.RoomRepository;
import repository.UserRepository;
import repository.jdbc.JdbcReservationRepository;
import repository.jdbc.JdbcRoomRepository;
import repository.jdbc.JdbcUserRepository;
import service.AuthService;
import service.ReservationService;
import service.RoomService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        DatabaseMigration.migrate();

        UserRepository userRepository =
                new JdbcUserRepository();

        AuthService authService =
                new AuthService(userRepository);

        RoomRepository roomRepository =
                new JdbcRoomRepository();

        RoomService roomService =
                new RoomService(roomRepository);

        ReservationRepository reservationRepository =
                new JdbcReservationRepository();

        ReservationService reservationService =
                new ReservationService(
                        reservationRepository,
                        roomRepository
                );

        User currentUser = null;

        boolean running = true;

        while (running) {

            if (currentUser == null) {

                currentUser =
                        authenticationMenu(
                                scanner,
                                authService
                        );

                if (currentUser == null) {
                    running = false;
                }

            } else {

                if (currentUser.getTitle()
                        == UserRole.CLIENT) {

                    boolean logout =
                            clientMenu(
                                    scanner,
                                    authService,
                                    roomService,
                                    reservationService,
                                    currentUser
                            );

                    if (logout) {
                        currentUser = null;
                    }

                } else {

                    boolean logout =
                            adminMenu(
                                    scanner,
                                    authService,
                                    roomService,
                                    reservationService,
                                    currentUser
                            );

                    if (logout) {
                        currentUser = null;
                    }
                }
            }
        }

        scanner.close();

        System.out.println(
                "\nGoodbye!"
        );
    }

    private static User authenticationMenu(
            Scanner scanner,
            AuthService authService
    ) {

        while (true) {

            System.out.println();
            System.out.println("========================");
            System.out.println("     HOTEL BOOKING");
            System.out.println("========================");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");

            System.out.print("Choice: ");

            int choice =
                    readSafeInt(scanner);

            switch (choice) {

                case 1:

                    System.out.println(
                            "\n===== REGISTER ====="
                    );

                    System.out.print(
                            "Full name: "
                    );

                    String fullName =
                            scanner.nextLine();

                    System.out.print(
                            "Email: "
                    );

                    String email =
                            scanner.nextLine();

                    System.out.print(
                            "Phone: "
                    );

                    String phone =
                            scanner.nextLine();

                    System.out.print(
                            "Password: "
                    );

                    String password =
                            scanner.nextLine();

                    System.out.print(
                            "Ville: "
                    );

                    String ville =
                            scanner.nextLine();

                    try {

                        User user =
                                authService.register(
                                        fullName,
                                        email,
                                        phone,
                                        password,
                                        ville
                                );

                        System.out.println(
                                "\nRegistration successful!"
                        );

                        return user;

                    } catch (RuntimeException e) {

                        System.out.println(
                                "\nError: "
                                        + e.getMessage()
                        );
                    }

                    break;

                case 2:

                    System.out.println(
                            "\n===== LOGIN ====="
                    );

                    System.out.print(
                            "Email: "
                    );

                    String loginEmail =
                            scanner.nextLine();

                    System.out.print(
                            "Password: "
                    );

                    String loginPassword =
                            scanner.nextLine();

                    try {

                        User user =
                                authService.login(
                                        loginEmail,
                                        loginPassword
                                );

                        System.out.println(
                                "\nLogin successful!"
                        );

                        System.out.println(
                                "Welcome "
                                        + user.getFullName()
                        );

                        System.out.println(
                                "Role: "
                                        + user.getTitle()
                        );

                        return user;

                    } catch (RuntimeException e) {

                        System.out.println(
                                "\nError: "
                                        + e.getMessage()
                        );
                    }

                    break;

                case 0:

                    return null;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static boolean clientMenu(
            Scanner scanner,
            AuthService authService,
            RoomService roomService,
            ReservationService reservationService,
            User currentUser
    ) {

        while (true) {

            System.out.println();
            System.out.println("==============================");
            System.out.println("         CLIENT MENU");
            System.out.println("==============================");

            System.out.println(
                    "Welcome "
                            + currentUser.getFullName()
            );

            System.out.println();

            System.out.println(
                    "1. View available rooms"
            );

            System.out.println(
                    "2. Create reservation"
            );

            System.out.println(
                    "3. My reservations"
            );

            System.out.println(
                    "4. Update my reservation"
            );

            System.out.println(
                    "5. Cancel my reservation"
            );

            System.out.println(
                    "9. Logout"
            );

            System.out.println(
                    "0. Exit"
            );

            System.out.print(
                    "Choice: "
            );

            int choice =
                    readSafeInt(scanner);

            switch (choice) {

                case 1:

                    displayAvailableRooms(
                            roomService
                    );

                    break;

                case 2:

                    createReservation(
                            scanner,
                            reservationService,
                            roomService,
                            currentUser
                    );

                    break;

                case 3:

                    viewMyReservations(
                            reservationService,
                            currentUser
                    );

                    break;

                case 4:

                    updateReservation(
                            scanner,
                            reservationService,
                            currentUser
                    );

                    break;

                case 5:

                    cancelReservation(
                            scanner,
                            reservationService,
                            currentUser
                    );

                    break;

                case 9:

                    authService.logout();

                    return true;

                case 0:

                    return true;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static void displayAvailableRooms(
            RoomService roomService
    ) {

        System.out.println(
                "\n===== AVAILABLE ROOMS ====="
        );

        try {

            List<Room> rooms =
                    roomService.getAvailableRooms();

            displayRooms(rooms);

        } catch (RuntimeException e) {

            System.out.println(
                    "Error: "
                            + e.getMessage()
            );
        }
    }

    private static void createReservation(
            Scanner scanner,
            ReservationService reservationService,
            RoomService roomService,
            User currentUser
    ) {

        System.out.println(
                "\n===== CREATE RESERVATION ====="
        );

        try {

            List<Room> rooms =
                    roomService.getAvailableRooms();

            if (rooms.isEmpty()) {

                System.out.println(
                        "No rooms available."
                );

                return;
            }

            displayRooms(rooms);

            System.out.print(
                    "Room ID: "
            );

            UUID roomId =
                    UUID.fromString(
                            scanner.nextLine().trim()
                    );

            System.out.print(
                    "Check-in (YYYY-MM-DD): "
            );

            LocalDate checkIn =
                    LocalDate.parse(
                            scanner.nextLine().trim()
                    );

            System.out.print(
                    "Check-out (YYYY-MM-DD): "
            );

            LocalDate checkOut =
                    LocalDate.parse(
                            scanner.nextLine().trim()
                    );

            System.out.print(
                    "Number of guests: "
            );

            int guestsCount =
                    readSafeInt(scanner);

            Reservation reservation =
                    reservationService.createReservation(
                            currentUser,
                            roomId,
                            checkIn,
                            checkOut,
                            guestsCount
                    );

            System.out.println();
            System.out.println(
                    "Reservation created successfully!"
            );

            System.out.println(
                    "Code: "
                            + reservation.getCode()
            );

            System.out.println(
                    "Check-in: "
                            + reservation.getCheckIn()
            );

            System.out.println(
                    "Check-out: "
                            + reservation.getCheckOut()
            );

            System.out.println(
                    "Guests: "
                            + reservation.getGuestsCount()
            );

            System.out.println(
                    "Total: "
                            + reservation.getTotalPrice()
                            + " DH"
            );

            System.out.println(
                    "Status: "
                            + reservation.getStatus()
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "\nError: "
                            + e.getMessage()
            );
        }
    }

    private static void viewMyReservations(
            ReservationService reservationService,
            User currentUser
    ) {

        System.out.println(
                "\n===== MY RESERVATIONS ====="
        );

        try {

            List<Reservation> reservations =
                    reservationService.getMyReservations(
                            currentUser
                    );

            if (reservations.isEmpty()) {

                System.out.println(
                        "You have no reservations."
                );

                return;
            }

            for (Reservation reservation :
                    reservations) {

                System.out.println(
                        "--------------------------------"
                );

                System.out.println(
                        "Code: "
                                + reservation.getCode()
                );

                System.out.println(
                        "Room ID: "
                                + reservation.getRoomId()
                );

                System.out.println(
                        "Check-in: "
                                + reservation.getCheckIn()
                );

                System.out.println(
                        "Check-out: "
                                + reservation.getCheckOut()
                );

                System.out.println(
                        "Guests: "
                                + reservation.getGuestsCount()
                );

                System.out.println(
                        "Total: "
                                + reservation.getTotalPrice()
                                + " DH"
                );

                System.out.println(
                        "Status: "
                                + reservation.getStatus()
                );
            }

            System.out.println(
                    "--------------------------------"
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "Error: "
                            + e.getMessage()
            );
        }
    }

    private static void updateReservation(
            Scanner scanner,
            ReservationService reservationService,
            User currentUser
    ) {

        System.out.println(
                "\n===== UPDATE RESERVATION ====="
        );

        try {

            System.out.print(
                    "Reservation code: "
            );

            String code =
                    scanner.nextLine().trim();

            Reservation reservation =
                    reservationService.getReservationByCode(
                            currentUser,
                            code
                    );

            System.out.println(
                    "Current check-in: "
                            + reservation.getCheckIn()
            );

            System.out.println(
                    "Current check-out: "
                            + reservation.getCheckOut()
            );

            System.out.println(
                    "Current guests: "
                            + reservation.getGuestsCount()
            );

            System.out.print(
                    "New check-in (YYYY-MM-DD): "
            );

            LocalDate checkIn =
                    LocalDate.parse(
                            scanner.nextLine().trim()
                    );

            System.out.print(
                    "New check-out (YYYY-MM-DD): "
            );

            LocalDate checkOut =
                    LocalDate.parse(
                            scanner.nextLine().trim()
                    );

            System.out.print(
                    "New number of guests: "
            );

            int guests =
                    readSafeInt(scanner);

            reservationService.updateReservation(
                    currentUser,
                    code,
                    checkIn,
                    checkOut,
                    guests
            );

            System.out.println(
                    "\nReservation updated successfully."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "\nError: "
                            + e.getMessage()
            );
        }
    }

    private static void cancelReservation(
            Scanner scanner,
            ReservationService reservationService,
            User currentUser
    ) {

        System.out.println(
                "\n===== CANCEL RESERVATION ====="
        );

        try {

            System.out.print(
                    "Reservation code: "
            );

            String code =
                    scanner.nextLine().trim();

            reservationService.cancelReservation(
                    currentUser,
                    code
            );

            System.out.println(
                    "\nReservation cancelled successfully."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "\nError: "
                            + e.getMessage()
            );
        }
    }

    private static boolean adminMenu(
            Scanner scanner,
            AuthService authService,
            RoomService roomService,
            ReservationService reservationService,
            User currentUser
    ) {

        while (true) {

            System.out.println();
            System.out.println("==============================");
            System.out.println("          ADMIN MENU");
            System.out.println("==============================");

            System.out.println(
                    "1. Create room"
            );

            System.out.println(
                    "2. View rooms"
            );

            System.out.println(
                    "3. Update room"
            );

            System.out.println(
                    "4. Delete room"
            );

            System.out.println(
                    "5. View all reservations"
            );

            System.out.println(
                    "6. Logout"
            );

            System.out.println(
                    "0. Exit"
            );

            System.out.print(
                    "Choice: "
            );

            int choice =
                    readSafeInt(scanner);

            switch (choice) {

                case 1:

                    createRoom(
                            scanner,
                            roomService,
                            currentUser
                    );

                    break;

                case 2:

                    viewRooms(
                            roomService,
                            currentUser
                    );

                    break;

                case 3:

                    updateRoom(
                            scanner,
                            roomService,
                            currentUser
                    );

                    break;

                case 4:

                    deleteRoom(
                            scanner,
                            roomService,
                            currentUser
                    );

                    break;

                case 5:

                    viewAllReservations(
                            reservationService,
                            currentUser
                    );

                    break;

                case 6:

                    authService.logout();

                    return true;

                case 0:

                    return true;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static void viewAllReservations(
            ReservationService reservationService,
            User currentUser
    ) {

        System.out.println(
                "\n===== ALL RESERVATIONS ====="
        );

        try {

            List<Reservation> reservations =
                    reservationService.getAllReservations(
                            currentUser
                    );

            if (reservations.isEmpty()) {

                System.out.println(
                        "No reservations found."
                );

                return;
            }

            for (Reservation reservation :
                    reservations) {

                System.out.println(
                        "--------------------------------"
                );

                System.out.println(
                        "Code: "
                                + reservation.getCode()
                );

                System.out.println(
                        "User ID: "
                                + reservation.getUserId()
                );

                System.out.println(
                        "Room ID: "
                                + reservation.getRoomId()
                );

                System.out.println(
                        "Check-in: "
                                + reservation.getCheckIn()
                );

                System.out.println(
                        "Check-out: "
                                + reservation.getCheckOut()
                );

                System.out.println(
                        "Guests: "
                                + reservation.getGuestsCount()
                );

                System.out.println(
                        "Total: "
                                + reservation.getTotalPrice()
                                + " DH"
                );

                System.out.println(
                        "Status: "
                                + reservation.getStatus()
                );
            }

            System.out.println(
                    "--------------------------------"
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "Error: "
                            + e.getMessage()
            );
        }
    }

    private static void createRoom(
            Scanner scanner,
            RoomService roomService,
            User currentUser
    ) {

        System.out.println(
                "\n===== CREATE ROOM ====="
        );

        try {

            System.out.print(
                    "Room number: "
            );

            int number =
                    readSafeInt(scanner);

            System.out.println(
                    "1. SINGLE"
            );

            System.out.println(
                    "2. DOUBLE"
            );

            System.out.println(
                    "3. SUITE"
            );

            System.out.print(
                    "Room type: "
            );

            int typeChoice =
                    readSafeInt(scanner);

            RoomType type;

            switch (typeChoice) {

                case 1:
                    type = RoomType.SINGLE;
                    break;

                case 2:
                    type = RoomType.DOUBLE;
                    break;

                case 3:
                    type = RoomType.SUITE;
                    break;

                default:
                    System.out.println(
                            "Invalid room type."
                    );
                    return;
            }

            System.out.print(
                    "Price: "
            );

            BigDecimal price =
                    new BigDecimal(
                            scanner.nextLine().trim()
                    );

            Room room =
                    new Room(
                            UUID.randomUUID(),
                            number,
                            type,
                            price,
                            RoomStatus.AVAILABLE
                    );

            roomService.createRoom(
                    currentUser,
                    room
            );

            System.out.println(
                    "Room created successfully."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "Error: "
                            + e.getMessage()
            );
        }
    }

    private static void viewRooms(
            RoomService roomService,
            User currentUser
    ) {

        try {

            List<Room> rooms =
                    roomService.getAllRoom(
                            currentUser
                    );

            displayRooms(rooms);

        } catch (RuntimeException e) {

            System.out.println(
                    "Error: "
                            + e.getMessage()
            );
        }
    }

    private static void updateRoom(
            Scanner scanner,
            RoomService roomService,
            User currentUser
    ) {

        System.out.println(
                "\n===== UPDATE ROOM ====="
        );

        try {

            System.out.print(
                    "Room ID: "
            );

            UUID id =
                    UUID.fromString(
                            scanner.nextLine().trim()
                    );

            Room room =
                    roomService.getRoomById(
                            currentUser,
                            id
                    );

            System.out.print(
                    "New number: "
            );

            room.setNumber(
                    readSafeInt(scanner)
            );

            System.out.print(
                    "New price: "
            );

            room.setPrice(
                    new BigDecimal(
                            scanner.nextLine().trim()
                    )
            );

            roomService.updateRoom(
                    currentUser,
                    room
            );

            System.out.println(
                    "Room updated successfully."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "Error: "
                            + e.getMessage()
            );
        }
    }

    private static void deleteRoom(
            Scanner scanner,
            RoomService roomService,
            User currentUser
    ) {

        System.out.println(
                "\n===== DELETE ROOM ====="
        );

        try {

            System.out.print(
                    "Room ID: "
            );

            UUID id =
                    UUID.fromString(
                            scanner.nextLine().trim()
                    );

            roomService.deleteRoom(
                    currentUser,
                    id
            );

            System.out.println(
                    "Room deleted successfully."
            );

        } catch (RuntimeException e) {

            System.out.println(
                    "Error: "
                            + e.getMessage()
            );
        }
    }

    private static void displayRooms(
            List<Room> rooms
    ) {

        if (rooms.isEmpty()) {

            System.out.println(
                    "No rooms found."
            );

            return;
        }

        for (Room room : rooms) {

            System.out.println(
                    "--------------------------------"
            );

            System.out.println(
                    "ID: "
                            + room.getId()
            );

            System.out.println(
                    "Room number: "
                            + room.getNumber()
            );

            System.out.println(
                    "Type: "
                            + room.getType()
            );

            System.out.println(
                    "Price: "
                            + room.getPrice()
                            + " DH"
            );

            System.out.println(
                    "Status: "
                            + room.getStatus()
            );
        }

        System.out.println(
                "--------------------------------"
        );
    }

    private static int readSafeInt(
            Scanner scanner
    ) {

        try {

            return Integer.parseInt(
                    scanner.nextLine().trim()
            );

        } catch (NumberFormatException e) {

            return -1;
        }
    }
}