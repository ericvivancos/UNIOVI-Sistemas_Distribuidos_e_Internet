package com.example.sdi2324entrega192.validators;

import com.example.sdi2324entrega192.entities.Post;
import com.example.sdi2324entrega192.services.PostService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
/**
 * Clase para validar el formulario de publicación.
 */
@Component
public class PostFormValidator implements Validator {

    /** Servicio de publicaciones */
    private final PostService postService;

    /**
     * Constructor de la clase PostFormValidator.
     *
     * @param postService El servicio de publicaciones.
     */
    public PostFormValidator(PostService postService){
        this.postService = postService;
    }

    /**
     * Indica si este Validador soporta la clase especificada.
     *
     * @param clazz La clase a ser validada.
     * @return true si la clase es soportada, false de lo contrario.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Post.class.equals(clazz);
    }

    /**
     * Valida el objeto de destino y registra cualquier error encontrado.
     *
     * @param target El objeto a ser validado.
     * @param errors Los errores encontrados durante la validación.
     */
    @Override
    public void validate(Object target, Errors errors) {
        Post post = (Post) target;

        // Validación de campos vacíos
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"title","Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"content","Error.empty");
    }
}