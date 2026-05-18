package com.food.ordering.system.foodOrderingSystem.config;

import com.food.ordering.system.foodOrderingSystem.entity.Location;
import com.food.ordering.system.foodOrderingSystem.entity.Role;
import com.food.ordering.system.foodOrderingSystem.entity.User;
import com.food.ordering.system.foodOrderingSystem.repository.LocationRepository;
import com.food.ordering.system.foodOrderingSystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminName;

    public DataSeeder(UserRepository userRepository,
                      LocationRepository locationRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.admin.email}") String adminEmail,
                      @Value("${app.admin.password}") String adminPassword,
                      @Value("${app.admin.name}") String adminName) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminName = adminName;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedLocations();
    }

    private void seedAdmin() {
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }
        User admin = new User();
        admin.setName(adminName);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        log.info("Seeded default admin user: {}", adminEmail);
    }

    private void seedLocations() {
        if (locationRepository.count() > 0) {
            return;
        }
        List<Location> defaults = List.of(
                buildLocation("Hanumakonda", "Ywca, Ashoka Rd, Hanumakonda", 1),
                buildLocation("Warangal", "Hunter Rd, Warangal", 2),
                buildLocation("Kazipet", "Station Rd, Kazipet", 3),
                buildLocation("Hyderabad", "Tank Bund Rd, Hyderabad", 4)
        );
        locationRepository.saveAll(defaults);
        log.info("Seeded {} default locations", defaults.size());
    }

    private Location buildLocation(String name, String label, int sortOrder) {
        Location l = new Location();
        l.setName(name);
        l.setLabel(label);
        l.setSortOrder(sortOrder);
        return l;
    }
}
