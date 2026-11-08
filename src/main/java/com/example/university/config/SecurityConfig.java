package com.example.university.config;

import com.example.university.repository.AppUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // enables @PreAuthorize on the REST controllers
public class SecurityConfig {

    /**
     * Chain 1: the REST API (/api/**).
     * Clients send username + password with every request (HTTP Basic). No session, no CSRF token.
     */
    @Bean
    @Order(1)
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admins/**").hasRole("ADMIN")
                .requestMatchers("/api/teachers/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/students/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .anyRequest().denyAll())
            .httpBasic(Customizer.withDefaults())
            // logged in, but wrong role -> 403 as JSON
            .exceptionHandling(ex -> ex.accessDeniedHandler((request, response, e) -> {
                response.setStatus(403);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":403,\"message\":\"Your role is not allowed to do this.\"}");
            }));
        return http.build();
    }

    /** Chain 2: the website (Thymeleaf pages) with a login form, a session and a logout button. */
    @Bean
    @Order(2)
    SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/error", "/css/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/teacher/**").hasRole("TEACHER")
                .requestMatchers("/student/**").hasRole("STUDENT")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")              // our own page: templates/login.html
                .defaultSuccessUrl("/", true)     // then AuthController sends each role to its page
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")             // POST from the "Log out" button
                .logoutSuccessUrl("/login?logout")
                .permitAll());
        return http.build();
    }

    /** Passwords are never stored as plain text. */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Tells Spring Security how to load a user from our database. */
    @Bean
    UserDetailsService userDetailsService(AppUserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPassword())
                        .roles(u.getRole().name()) // becomes ROLE_ADMIN / ROLE_TEACHER / ROLE_STUDENT
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
