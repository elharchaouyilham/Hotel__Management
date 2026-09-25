package service;

import model.User;
import model.enums.UserRole;
import repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class AuthService {

    private final UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(
            String fullName,
            String email,
            String phone,
            String password,
            String ville
    ) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "Email déjà utilisé."
            );
        }

        int bf = 10;

        String hashedPassword = BCrypt.hashpw(
                password,
                BCrypt.gensalt(bf)
        );

        User user = new User(
                UUID.randomUUID(),
                fullName,
                email,
                phone,
                hashedPassword,
                ville,
                UserRole.CLIENT
        );

        userRepository.save(user);

        return user;
    }

    public User login(
            String email,
            String password
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Email ou mot de passe incorrect."
                        )
                );

        if (!BCrypt.checkpw(
                password,
                user.getPasswordHash()
        )) {

            throw new IllegalArgumentException(
                    "Email ou mot de passe incorrect."
            );
        }

        currentUser = user;

        return user;
    }

    public void logout() {

        currentUser = null;
    }
}