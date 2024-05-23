package com.example.sdi2324entrega192.validators;

import com.example.sdi2324entrega192.entities.User;
import com.example.sdi2324entrega192.services.UsersService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase para validar el formulario de registro de usuario.
 */
@Component
public class SignUpFormValidator implements Validator {

    /** Servicio de usuarios */
    private final UsersService usersService;

    /**
     * Constructor de la clase SignUpFormValidator.
     *
     * @param usersService El servicio de usuarios.
     */
    public SignUpFormValidator(UsersService usersService){
        this.usersService = usersService;
    }
    @Override
    public boolean supports(Class<?>aClass){
        return User.class.equals(aClass);
    }

    /**
     * Valida el objeto de destino y registra cualquier error encontrado.
     *
     * @param target El objeto a ser validado.
     * @param errors Los errores encontrados durante la validación.
     */    @Override
    public void validate(Object target, Errors errors){
        User user = (User) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"email","Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "Error.empty");


        if (!isValidEmail(user.getEmail())) {
            errors.rejectValue("email", "Error.signup.email.format");
        }

        if (usersService.getUserByEmail(user.getEmail()) != null) {
            errors.rejectValue("email", "Error.signup.email.duplicate");
        }

        if (!isStrongPassword(user.getPassword())) {
            errors.rejectValue("password", "Error.signup.password.weak");
        }

        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirm",
                    "Error.signup.passwordConfirm.coincidence");}

    }
    /**
     * Verifica si un email tiene un formato válido.
     *
     * @param email El email a ser validado.
     * @return true si el email tiene un formato válido, false de lo contrario.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Verifica si una contraseña es lo suficientemente fuerte.
     *
     * @param password La contraseña a ser validada.
     * @return true si la contraseña es lo suficientemente fuerte, false de lo contrario.
     */
    private boolean isStrongPassword(String password) {

        String uppercaseRegex = ".*[A-Z].*";
        String lowercaseRegex = ".*[a-z].*";
        String digitRegex = ".*\\d.*";
        String specialCharacterRegex = ".*[@#$%^&+=\\[\\]{}\\\\|:;\"',<.>/?¿¡·()`´ç~_ºª-].*";




        if (password.length() < 12 ||
                !password.matches(uppercaseRegex) ||
                !password.matches(lowercaseRegex) ||
                !password.matches(digitRegex) ||
                !password.matches(specialCharacterRegex)) {
            return false;
        }

        return true;
    }

}
