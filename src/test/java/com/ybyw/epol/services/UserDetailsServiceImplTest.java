package com.ybyw.epol.services;

import com.ybyw.epol.entity.User;
import com.ybyw.epol.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl userDetailsService;

    // This method runs before each test to initialize the mocked dependencies and the service under test
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
        // Arrange: Set up the mock user and define the behavior of the mock repository
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        // Arrange
        User mockUser = new User();
        mockUser.setName("testuser");
        mockUser.setPassword("password123");

        // When the repository is called with the email, return the mock user
        when(userRepository.findFirstByEmail("test@example.com"))
                .thenReturn(Optional.of(mockUser));

        // Act: Call the method under test
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Assert: Verify that the returned UserDetails matches the mock user
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        // Verify that the repository method was called exactly once
        verify(userRepository, times(1)).findFirstByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        // Arrange: Set the repository to return empty for a non-existent user
        when(userRepository.findFirstByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert: Expect an exception when the user is not found
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("notfound@example.com")
        );

        // Check that the exception message matches the expected one
        assertEquals("Username not found", exception.getMessage());
        verify(userRepository, times(1)).findFirstByEmail("notfound@example.com");
    }
}
