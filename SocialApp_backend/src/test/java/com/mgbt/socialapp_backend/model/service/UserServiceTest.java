package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.repository.IUserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserApp juan;
    private UserApp john;
    private UserApp albert;
    private UserApp laura;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        this.juan = new UserApp(1L, "jpablo", "Juan", "Pablo", null, null, new Date(), new Date(), false, "Connected");
        this.john = new UserApp(2L, "jdoe", "John", "Doe", null, null, new Date(), new Date(), true, "Connected");
        this.albert = new UserApp(3L, "awesker", "Albert", "Wesker", null, null, new Date(), new Date(), true, "Connected");
        this.laura = new UserApp(4L, "lflores", "Laura", "Flores", null, null, new Date(), new Date(), false, "Connected");
    }

    /*
      These methods are used to achieve a synchronisation between the Keycloak Database and MySQL.
      When the application is started, it will search in the database if the user exists and, if it does,
      the method will be called to check the synchronisation (since the user can change his name and
      surname from the Keycloak control panel).
      Note: The user cannot change his ID or username.
    */
    @Test
    void returnJuanWhenTheKeycloakUserHasTheSameUsername() {
        /*
          If the first and last name coming from the Keycloak token are the same as
          the found user, it does nothing.
        */
        UserApp keycloakUser = new UserApp();
        keycloakUser.setUsername("jpablo");
        keycloakUser.setName("Juan");
        keycloakUser.setSurname("Pablo");
        userService.checkIfUserIsPersisted(this.juan, keycloakUser);
        verify(userRepository, times(0)).save(this.juan);
        assertEquals(1L, this.juan.getIdUser());
        assertEquals("jpablo", this.juan.getUsername());
        assertEquals("Juan", this.juan.getName());
        assertEquals("Pablo", this.juan.getSurname());
    }
    @Test
    void returnTheKeycloakUserWhenTheirNamesAreNotEqual() {
        /*
          If the first and last name coming from the Keycloak token are the not same as
          the found user, it updates them.
        */
        UserApp keycloakUser = new UserApp();
        keycloakUser.setUsername("jpablo");
        keycloakUser.setName("Chris");
        keycloakUser.setSurname("Redfield");
        userService.checkIfUserIsPersisted(this.juan, keycloakUser);
        verify(userRepository, times(1)).save(this.juan);
        assertEquals(1L, this.juan.getIdUser());
        assertEquals("jpablo", this.juan.getUsername());
        assertEquals("Chris", this.juan.getName());
        assertEquals("Redfield", this.juan.getSurname());
    }
}