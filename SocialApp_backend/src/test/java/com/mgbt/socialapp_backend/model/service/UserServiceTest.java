package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import com.mgbt.socialapp_backend.model.repository.IUserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserApp userFounded;
    private UserApp userKeycloakToken;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        this.userFounded = new UserApp();
        this.userFounded.setIdUser(5L);
        this.userFounded.setUsername("user");
        this.userFounded.setName("John");
        this.userFounded.setSurname("Doe");
        this.userKeycloakToken = new UserApp();
    }

    @Test
    void returnTheFoundedUserWhenTheirNamesAreEqual() {
        this.userKeycloakToken.setName("John");
        this.userKeycloakToken.setSurname("Doe");
        userService.checkIfUserIsPersisted(this.userFounded, this.userKeycloakToken);
        verify(userRepository, times(0)).save(this.userFounded);
        assertEquals(5L, this.userFounded.getIdUser());
        assertEquals("user", this.userFounded.getUsername());
        assertEquals("John", this.userFounded.getName());
        assertEquals("Doe", this.userFounded.getSurname());
    }

    @Test
    void returnTheKeycloakUserWhenTheirNamesAreNotEqual() {
        this.userKeycloakToken.setName("Albert");
        this.userKeycloakToken.setSurname("Wesker");
        userService.checkIfUserIsPersisted(this.userFounded, this.userKeycloakToken);
        verify(userRepository, times(1)).save(this.userFounded);
        assertEquals(5L, this.userFounded.getIdUser());
        assertEquals("user", this.userFounded.getUsername());
        assertNotEquals("John", this.userFounded.getName());
        assertNotEquals("Doe", this.userFounded.getSurname());
        assertEquals("Albert", this.userFounded.getName());
        assertEquals("Wesker", this.userFounded.getSurname());
    }
}