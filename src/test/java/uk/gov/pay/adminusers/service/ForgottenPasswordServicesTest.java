package uk.gov.pay.adminusers.service;

import org.junit.Before;
import org.junit.Test;
import uk.gov.pay.adminusers.model.ForgottenPassword;
import uk.gov.pay.adminusers.persistence.dao.ForgottenPasswordDao;
import uk.gov.pay.adminusers.persistence.dao.UserDao;
import uk.gov.pay.adminusers.persistence.entity.ForgottenPasswordEntity;
import uk.gov.pay.adminusers.persistence.entity.UserEntity;

import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ForgottenPasswordServicesTest {

    ForgottenPasswordServices forgottenPasswordServices;
    ForgottenPasswordDao forgottenPasswordDao;
    UserDao userDao;

    @Before
    public void before() throws Exception {
        userDao = mock(UserDao.class);
        forgottenPasswordDao = mock(ForgottenPasswordDao.class);
        forgottenPasswordServices = new ForgottenPasswordServices(userDao, forgottenPasswordDao, new LinksBuilder("http://localhost"));
    }

    @Test
    public void shouldReturnANewForgottenPassword_whenCreating_ifUserFound() throws Exception {

        String existingUser = "existing-user";
        UserEntity mockUser = mock(UserEntity.class);
        when(mockUser.getUsername()).thenReturn(existingUser);

        when(userDao.findByUsername(existingUser)).thenReturn(Optional.of(mockUser));
        Optional<ForgottenPassword> forgottenPasswordOptional = forgottenPasswordServices.create(existingUser);

        verify(forgottenPasswordDao, times(1)).persist(any(ForgottenPasswordEntity.class));
        assertTrue(forgottenPasswordOptional.isPresent());
        assertThat(forgottenPasswordOptional.get().getUsername(), is(existingUser));
        assertThat(forgottenPasswordOptional.get().getCode(), is(notNullValue()));
        assertThat(forgottenPasswordOptional.get().getLinks().size(), is(1));

    }

    @Test
    public void shouldReturnEmpty_whenCreating_ifUserNotFound() throws Exception {
        String nonExistingUser = "non-existent-user";
        when(userDao.findByUsername(nonExistingUser)).thenReturn(Optional.empty());

        Optional<ForgottenPassword> forgottenPasswordOptional = forgottenPasswordServices.create(nonExistingUser);
        assertFalse(forgottenPasswordOptional.isPresent());
    }
}