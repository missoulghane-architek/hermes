package com.m2it.hermes.infrastructure.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.m2it.hermes.domain.model.Permission;
import com.m2it.hermes.domain.model.Property;
import com.m2it.hermes.domain.model.PropertyStatus;
import com.m2it.hermes.domain.model.PropertyType;
import com.m2it.hermes.domain.model.Role;
import com.m2it.hermes.domain.model.User;
import com.m2it.hermes.domain.port.out.PasswordEncoder;
import com.m2it.hermes.domain.port.out.PermissionRepository;
import com.m2it.hermes.domain.port.out.PropertyRepository;
import com.m2it.hermes.domain.port.out.RoleRepository;
import com.m2it.hermes.domain.port.out.UserRepository;
import com.m2it.hermes.domain.valueobject.Address;
import com.m2it.hermes.domain.valueobject.Email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initializing default data...");

        createPermissions();
        createRoles();
        createDefaultAdmin();

        log.info("Default data initialized successfully!");
    }

    private void createPermissions() {
        List<Permission> permissions = Arrays.asList(
                createPermission("USER_READ", "Read user information"),
                createPermission("USER_WRITE", "Create and update users"),
                createPermission("USER_DELETE", "Delete users"),
                createPermission("ROLE_READ", "Read role information"),
                createPermission("ROLE_WRITE", "Create and update roles"),
                createPermission("ROLE_DELETE", "Delete roles"),
                createPermission("PERMISSION_READ", "Read permission information"),
                createPermission("PERMISSION_WRITE", "Create and update permissions"),
                createPermission("PERMISSION_DELETE", "Delete permissions")
        );

        permissions.forEach(permission -> {
            if (!permissionRepository.existsByName(permission.getName())) {
                permissionRepository.save(permission);
                log.info("Created permission: {}", permission.getName());
            }
        });
    }

    private void createRoles() {
        Role userRole = createRole(
                "ROLE_USER",
                "Default user role",
                Arrays.asList("USER_READ")
        );

        Role adminRole = createRole(
                "ROLE_ADMIN",
                "Administrator role with full access",
                Arrays.asList(
                        "USER_READ", "USER_WRITE", "USER_DELETE",
                        "ROLE_READ", "ROLE_WRITE", "ROLE_DELETE",
                        "PERMISSION_READ", "PERMISSION_WRITE", "PERMISSION_DELETE"
                )
        );

        if (!roleRepository.existsByName(userRole.getName())) {
            roleRepository.save(userRole);
            log.info("Created role: {}", userRole.getName());
        }

        if (!roleRepository.existsByName(adminRole.getName())) {
            roleRepository.save(adminRole);
            log.info("Created role: {}", adminRole.getName());
        }
    }

    private void createDefaultAdmin() {
        String adminUsername = "admin";

        if (userRepository.existsByUsername(adminUsername)) {
            log.info("Admin user already exists");
            return;
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        LocalDateTime now = LocalDateTime.now();
        User admin = User.builder()
                .id(UUID.randomUUID())
                .username(adminUsername)
                .firstName("Doe")
                .lastName("Joy")
                .email(new Email("admin@hermes.com"))
                .password(passwordEncoder.encode("admin123"))
                .enabled(true)
                .emailVerified(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        admin.addRole(adminRole);
        userRepository.save(admin);

        log.info("Created default admin user - Username: admin, Password: admin123");



        createProperty(admin.getId(), "Appart 1", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/2a/e5/3d/2ae53daf8e52c4a80d822a68f0c10072c50f753d.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/80/f6/0e/80f60e3745077f7c61bdbe1891338c60e9915ebc.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 2", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/cf/80/06/cf800672a945649803de1440c41c604d74d50022.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/43/5c/8f/435c8fae6bfe90a2cd1d0165abaeaa3acfdff9ea.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 3", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/f0/c6/2b/f0c62bbc273e0101f9497d340a0bf23d45f4bb2c.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/da/8c/63/da8c638f298540e8c284c0c00a7c8ae2d2c3dc1a.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 4", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
           "https://img.leboncoin.fr/api/v1/lbcpb1/images/b7/73/c1/b773c1b98684fbd4d7a32a19084406f5b7f948e7.jpg?rule=classified-1200x800-webp",
           "https://img.leboncoin.fr/api/v1/lbcpb1/images/b3/72/cf/b372cfc37dfcfb467c3f8680723ab5d4034d7831.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 5", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/62/4f/c3/624fc34c084c54477dc7452d6027b5bb10e4ef7a.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/85/6a/6f/856a6fa4d2c8085a6592ff7c28a77e7ff0232bfc.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 6", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
           "https://img.leboncoin.fr/api/v1/lbcpb1/images/ef/f3/6c/eff36cd960256fca0852ea19d390ce07efd9da87.jpg?rule=classified-1200x800-webp",
           "https://img.leboncoin.fr/api/v1/lbcpb1/images/a1/ee/b9/a1eeb9ac1e429c33f8fc393b5af5b6841c2018f2.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 7", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/09/23/6e/09236e70549173b7b091e07022abb66c43d4a72e.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/e9/09/27/e909274446b9f7f81a68794b153aec21dd840890.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 8", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/2a/e5/3d/2ae53daf8e52c4a80d822a68f0c10072c50f753d.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/80/f6/0e/80f60e3745077f7c61bdbe1891338c60e9915ebc.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 9", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/2a/e5/3d/2ae53daf8e52c4a80d822a68f0c10072c50f753d.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/80/f6/0e/80f60e3745077f7c61bdbe1891338c60e9915ebc.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 10", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/2a/e5/3d/2ae53daf8e52c4a80d822a68f0c10072c50f753d.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/80/f6/0e/80f60e3745077f7c61bdbe1891338c60e9915ebc.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 11", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/2a/e5/3d/2ae53daf8e52c4a80d822a68f0c10072c50f753d.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/80/f6/0e/80f60e3745077f7c61bdbe1891338c60e9915ebc.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 12", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/2a/e5/3d/2ae53daf8e52c4a80d822a68f0c10072c50f753d.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/80/f6/0e/80f60e3745077f7c61bdbe1891338c60e9915ebc.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 13", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/2a/e5/3d/2ae53daf8e52c4a80d822a68f0c10072c50f753d.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/80/f6/0e/80f60e3745077f7c61bdbe1891338c60e9915ebc.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 14", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/2a/e5/3d/2ae53daf8e52c4a80d822a68f0c10072c50f753d.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/80/f6/0e/80f60e3745077f7c61bdbe1891338c60e9915ebc.jpg?rule=classified-1200x800-webp"
        ));
        createProperty(admin.getId(), "Appart 15", "Bel appart 1", PropertyType.APARTMENT, PropertyStatus.AVAILABLE, Arrays.asList(
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/2a/e5/3d/2ae53daf8e52c4a80d822a68f0c10072c50f753d.jpg?rule=classified-1200x800-webp",
            "https://img.leboncoin.fr/api/v1/lbcpb1/images/80/f6/0e/80f60e3745077f7c61bdbe1891338c60e9915ebc.jpg?rule=classified-1200x800-webp"
        ));
    }

    private Permission createPermission(String name, String description) {
        LocalDateTime now = LocalDateTime.now();
        return Permission.builder()
                .id(UUID.randomUUID())
                .name(name)
                .description(description)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private Role createRole(String name, String description, List<String> permissionNames) {
        LocalDateTime now = LocalDateTime.now();
        Role role = Role.builder()
                .id(UUID.randomUUID())
                .name(name)
                .description(description)
                .createdAt(now)
                .updatedAt(now)
                .build();

        permissionNames.forEach(permissionName -> {
            Permission permission = permissionRepository.findByName(permissionName)
                    .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName));
            role.addPermission(permission);
        });

        return role;
    }

    private void createProperty(UUID ownerId, String name, String description, PropertyType propertyType, PropertyStatus status, List<String> images) {
        
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        Property prop = new Property(UUID.randomUUID(), ownerId, name, description, propertyType, new Address("Street","City","75001","Country"), new BigDecimal(100), status, images, new ArrayList<>(), createdAt, updatedAt);
        propertyRepository.save(prop);
    }
}
