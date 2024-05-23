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
 * Clase para validar el formulario de edición de usuarios.
 */
@Component
public class EditFormValidator implements Validator {

    /** Servicio de usuarios */
    private final UsersService usersService;

    /**
     * Constructor de la clase EditFormValidator.
     *
     * @param usersService El servicio de usuarios.
     */
    public EditFormValidator(UsersService usersService){
        this.usersService = usersService;
    }

    /**
     * Indica si este Validador soporta la clase especificada.
     *
     * @param aClass La clase a ser validada.
     * @return true si la clase es soportada, false de lo contrario.
     */
    @Override
    public boolean supports(Class<?> aClass){
        return User.class.equals(aClass);
    }

    /**
     * Valida el objeto de destino y registra cualquier error encontrado.
     *
     * @param target El objeto a ser validado.
     * @param errors Los errores encontrados durante la validación.
     */
    @Override
    public void validate(Object target, Errors errors){

        User user = (User) target;

        // Validación de campos vacíos
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"email","Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "Error.empty");

        // Validación del formato de correo electrónico
        if (!isValidEmail(user.getEmail())) {
            errors.rejectValue("email", "Error.signup.email.format");
        }

        // Validación de correo electrónico duplicado
        if (usersService.getUserByEmail(user.getEmail()) != null) {
            errors.rejectValue("email", "Error.signup.email.duplicate");
        }
    }

    /**
     * Verifica si una cadena de correo electrónico tiene un formato válido.
     *
     * @param email La cadena de correo electrónico a verificar.
     * @return true si el formato del correo electrónico es válido, false de lo contrario.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
