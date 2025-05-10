package com.ybyw.epol.services.auth;

import com.ybyw.epol.dto.SignupRequest;
import com.ybyw.epol.dto.UserDto;
import com.ybyw.epol.entity.User;
import com.ybyw.epol.enums.UserRole;
import com.ybyw.epol.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    // Mocked dependency for database operations
    @Mock
    private UserRepository userRepository;

    // Service under test with mocked dependencies injected
    @InjectMocks
    private AuthServiceImpl authService;

    // Used to capture the User object passed to repository save
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl();
        authService.userRepository = userRepository;
    }

    @Test
    void createUser_shouldSaveUserAndReturnDto() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setName("Test User");
        request.setPassword("password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@example.com");
        savedUser.setName("Test User");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserDto result = authService.createUser(request);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        verify(userRepository).save(userCaptor.capture());
        assertEquals(UserRole.LEARNER, userCaptor.getValue().getRole());
    }

    @Test
    void hasUserWithEmail_shouldReturnTrueIfUserExists() {
        // Arrange
        when(userRepository.findFirstByEmail("exists@example.com"))
                .thenReturn(Optional.of(new User()));

        // Act & Assert
        assertTrue(authService.hasUserWithEmail("exists@example.com"));
    }

    @Test
    void hasUserWithEmail_shouldReturnFalseIfUserDoesNotExist() {
        when(userRepository.findFirstByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        assertFalse(authService.hasUserWithEmail("notfound@example.com"));
    }

    @Test
    void createInstructorAccount_shouldCreateIfNotExists() {
        when(userRepository.findByRole(UserRole.INSTRUCTOR)).thenReturn(null);

        authService.createInstructorAccount();

        verify(userRepository).save(userCaptor.capture());
        assertEquals("instructor", userCaptor.getValue().getName());
        assertEquals(UserRole.INSTRUCTOR, userCaptor.getValue().getRole());
    }

    @Test
    void createInstructorAccount_shouldNotCreateIfExists() {
        when(userRepository.findByRole(UserRole.INSTRUCTOR)).thenReturn(new User());

        authService.createInstructorAccount();

        verify(userRepository, never()).save(any());
    }

    @Test
    void createAdminAccount_shouldCreateIfNotExists() {
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(null);

        authService.createAdminAccount();

        verify(userRepository).save(userCaptor.capture());
        assertEquals("admin", userCaptor.getValue().getName());
        assertEquals(UserRole.ADMIN, userCaptor.getValue().getRole());
    }

    @Test
    void createAdminAccount_shouldNotCreateIfExists() {
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(new User());

        authService.createAdminAccount();

        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers_shouldReturnAllUserDtos() {
        List<User> users = Arrays.asList(
                createUser(1L, "a@example.com", "User A"),
                createUser(2L, "b@example.com", "User B")
        );

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = authService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("User A", result.get(0).getName());
        assertEquals("User B", result.get(1).getName());
    }

    private User createUser(Long id, String email, String name) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        return user;
    }
}
