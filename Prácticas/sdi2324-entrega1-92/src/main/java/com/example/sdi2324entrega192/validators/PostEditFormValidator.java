package com.example.sdi2324entrega192.validators;

import com.example.sdi2324entrega192.entities.Post;
import com.example.sdi2324entrega192.services.PostService;
import com.example.sdi2324entrega192.services.PostStatusService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Arrays;

/**
 * Clase para validar el formulario de edición de publicaciones.
 */
@Component
public class PostEditFormValidator implements Validator {

    /** Servicio de publicaciones */
    private final PostService postService;

    /** Servicio de estados de publicaciones */
    private final PostStatusService postStatusService;

    /**
     * Constructor de la clase PostEditFormValidator.
     *
     * @param postService El servicio de publicaciones.
     * @param postStatusService El servicio de estados de publicaciones.
     */
    public PostEditFormValidator(PostService postService, PostStatusService postStatusService){
        this.postService = postService;
        this.postStatusService = postStatusService;
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

        // Validación de campo vacío
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "status", "Error.empty");

        // Validación de estado de publicación inválido
        if (post.getStatus() == null || Arrays.stream(postStatusService.getStatus()).noneMatch(s -> s.equals(post.getStatus()))) {
            errors.rejectValue("status", "Error.post.status.invalid");
        }

        // Validación de estado de publicación duplicado
        if(post.getStatus().equals(postService.getPostById(post.getId()).getStatus())){
            errors.rejectValue("status", "Error.post.status.replicate");
        }
    }
}
