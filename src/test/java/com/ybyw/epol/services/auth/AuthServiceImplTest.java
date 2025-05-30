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


    @Mock
    private UserRepository userRepository;

    // AuthServiceImpl will have the above mock injected for testing
    @InjectMocks
    private AuthServiceImpl authService;

    // Used to capture the actual User object passed to the repository's save method
    @Captor
    private ArgumentCaptor<User> userCaptor;

    // Initialize mocks and service before each test method
    @BeforeEach
    void setUp() {
        // Initialize mocks before each test
        MockitoAnnotations.openMocks(this);

        // Manually create the service instance (optional if using @InjectMocks alone)
        authService = new AuthServiceImpl(); // Manually instantiating the service

        // Manually inject the mocked repository into the service
        authService.userRepository = userRepository; // Injecting the mock manually
    }

    @Test
    void createUser_shouldSaveUserAndReturnDto() {
        // Arrange: Set up a new signup request
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setName("Test User");
        request.setPassword("password");

        // Define a mock user to simulate what the repository returns after saving
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@example.com");
        savedUser.setName("Test User");

        // Simulate repository behavior: when save is called, return the mock user
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act: Call the method under test
        UserDto result = authService.createUser(request);

        // Assert: Check returned DTO contains expected values
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());

        // Verify that save was called and capture the User object passed
        verify(userRepository).save(userCaptor.capture());

        // Check if the default user role was assigned as LEARNER
        assertEquals(UserRole.LEARNER, userCaptor.getValue().getRole());
    }

    @Test
    void hasUserWithEmail_shouldReturnTrueIfUserExists() {

        // Arrange - Simulate that a user exists for the given email
        when(userRepository.findFirstByEmail("exists@example.com"))
                .thenReturn(Optional.of(new User()));

        // Act & Assert: Method should return true if user is found
        assertTrue(authService.hasUserWithEmail("exists@example.com"));
    }

    @Test
    void hasUserWithEmail_shouldReturnFalseIfUserDoesNotExist() {

        // Arrange: Simulate no instructor exists in the DB
        when(userRepository.findFirstByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        // Assert: Method should return false
        assertFalse(authService.hasUserWithEmail("notfound@example.com"));
    }

    @Test
    void createInstructorAccount_shouldCreateIfNotExists() {
        // Simulate that no instructor exists in the DB
        when(userRepository.findByRole(UserRole.INSTRUCTOR)).thenReturn(null);

        // Act: Attempt to create instructor
        authService.createInstructorAccount();

        // Assert: Save should be called with a new User of role INSTRUCTOR
        verify(userRepository).save(userCaptor.capture());
        assertEquals("instructor", userCaptor.getValue().getName());
        assertEquals(UserRole.INSTRUCTOR, userCaptor.getValue().getRole());
    }

    @Test
    void createInstructorAccount_shouldNotCreateIfExists() {

        // Arrange: Simulate that an instructor already exists
        when(userRepository.findByRole(UserRole.INSTRUCTOR)).thenReturn(new User());

        // Act: Attempt to create instructor again
        authService.createInstructorAccount();

        // Assert: Save should not be called again
        verify(userRepository, never()).save(any());
    }

    @Test
    void createAdminAccount_shouldCreateIfNotExists() {

        // Arrange: Simulate admin role is not found in DB
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(null);

        // Act: Create default admin
        authService.createAdminAccount();

        // Assert: Save should be called and captured for assertions
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
            
        // Arrange: Create a list of users returned by the mock repository
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
