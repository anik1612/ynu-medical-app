package com.example.ynumedical;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class MockitoPresenterTests {
    @Mock
    LoginPage view;
    @Mock
    Model model;
    @Test
    public void presenterTest(){
        when(view.getEmail()).thenReturn("abc@mail.com");
        when(view.getPassword()).thenReturn("abcdef");
        when(model.userIsFound("abc@mail.com", "abcdef")).thenReturn(1);

        Presenter presenter = new Presenter(model, view);
        presenter.login();
        verify(view).displayMessage("trying to login");
    }

    @Test
    public void presenterTestEmptyEmail(){
        when(view.getEmail()).thenReturn("");
        when(view.getPassword()).thenReturn("abcdef");
        when(model.userIsFound("", "abcdef")).thenReturn(1);

        Presenter presenter = new Presenter(model, view);
        presenter.login();
        verify(view).displayMessage("email cannot be empty");
    }

    @Test
    public void presenterTestEmptyPassword(){
        when(view.getEmail()).thenReturn("abc@mail.com");
        when(view.getPassword()).thenReturn("");
        when(model.userIsFound("abc@mail.com", "")).thenReturn(1);

        Presenter presenter = new Presenter(model, view);
        presenter.login();
        verify(view).displayMessage("password cannot be empty");
    }

    @Test
    public void presenterTestShortPassword(){
        when(view.getEmail()).thenReturn("abc@mail.com");
        when(view.getPassword()).thenReturn("abc");
        when(model.userIsFound("abc@mail.com", "abc")).thenReturn(1);

        Presenter presenter = new Presenter(model, view);
        presenter.login();
        verify(view).displayMessage("The minimum password length is 6 characters");
    }

    @Test
    public void presenterTestInvalidLogin(){
        when(view.getEmail()).thenReturn("abc@mail.com");
        when(view.getPassword()).thenReturn("abcdef");
        when(model.userIsFound("abc@mail.com", "abcdef")).thenReturn(0);

        Presenter presenter = new Presenter(model, view);
        presenter.login();
        verify(view).displayMessage("invalid login");
    }
}