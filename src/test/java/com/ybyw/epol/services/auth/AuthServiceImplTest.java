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
        // Initialize mocks before each test
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(); // Manually instantiating the service
        authService.userRepository = userRepository; // Injecting the mock manually
    }

    @Test
    void createUser_shouldSaveUserAndReturnDto() {
        // Arrange
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setName("Test User");
        request.setPassword("password");

        // Define the behavior of save - returning a mocked saved user
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@example.com");
        savedUser.setName("Test User");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserDto result = authService.createUser(request);

        // Assert - Validate the result is not null and contains expected values
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());

        // Verify that save() was called and capture the user object passed
        verify(userRepository).save(userCaptor.capture());

        // Ensure the default role is correctly set as LEARNER
        assertEquals(UserRole.LEARNER, userCaptor.getValue().getRole());
    }

    @Test
    void hasUserWithEmail_shouldReturnTrueIfUserExists() {
        // Arrange - Simulate that a user exists for the given email
        when(userRepository.findFirstByEmail("exists@example.com"))
                .thenReturn(Optional.of(new User()));

        // Act & Assert - Verify that the method returns true
        assertTrue(authService.hasUserWithEmail("exists@example.com"));
    }

    @Test
    void hasUserWithEmail_shouldReturnFalseIfUserDoesNotExist() {
        // Simulate that no user is found for the email
        when(userRepository.findFirstByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        // Verify that the method returns false
        assertFalse(authService.hasUserWithEmail("notfound@example.com"));
    }

    @Test
    void createInstructorAccount_shouldCreateIfNotExists() {
        // Simulate that no instructor exists in the DB
        when(userRepository.findByRole(UserRole.INSTRUCTOR)).thenReturn(null);

        // Call the method to create instructor
        authService.createInstructorAccount();

        // Verify save was called and capture the instructor created
        verify(userRepository).save(userCaptor.capture());
        assertEquals("instructor", userCaptor.getValue().getName());
        assertEquals(UserRole.INSTRUCTOR, userCaptor.getValue().getRole());
    }

    @Test
    void createInstructorAccount_shouldNotCreateIfExists() {
        // Simulate instructor already exists
        when(userRepository.findByRole(UserRole.INSTRUCTOR)).thenReturn(new User());

        // Attempt to create instructor account
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
