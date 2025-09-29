package com.amouri_dev.talksy;

import com.amouri_dev.talksy.entities.role.Role;
import com.amouri_dev.talksy.infrastructure.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class TalksyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalksyApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(final RoleRepository roleRepository) {
		return args -> {
			final Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
			if (userRole.isEmpty()) {
				final Role role = new Role();
				role.setName("ROLE_USER");
				roleRepository.save(role);
			}
		};
	}
}
