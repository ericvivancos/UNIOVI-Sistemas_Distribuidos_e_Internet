package com.example.sdi2324entrega192.services;
import com.example.sdi2324entrega192.entities.FriendRequest;
import com.example.sdi2324entrega192.entities.Friendship;
import com.example.sdi2324entrega192.entities.Post;
import com.example.sdi2324entrega192.entities.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;

/**
 * esta clase separa la creacion de las peticiones de amistad , ya que sino te daba un error porque los
 * usuarios estaban detached , al separarlo en clases y con la etiqueta transacctional se usan transsaciones diferentes
 */
@Service
public class FriendshipInitializationService {

    private final FriendRequestService friendRequestService;
    private final UsersService usersService;
    private final PostService postService;
    public FriendshipInitializationService(FriendRequestService friendRequestService, UsersService usersService, PostService postService) {
        this.friendRequestService = friendRequestService;
        this.usersService = usersService;
        this.postService = postService;
    }
    @Transactional
    public void initializeFriendships() {
        User user11 = usersService.getUserByEmail("user11@email.com");

        // Agregar amistad entre user1 y user2
        User user2 = usersService.getUserByEmail("user02@email.com");
         friendRequestService.sendInvite(user2, user11);
        // Agregar más amistades con el user 2
        User user13= usersService.getUserByEmail("user13@email.com");
        friendRequestService.sendInvite(user13,user11);
        //9 y 2
        User user9 = usersService.getUserByEmail("user09@email.com");
        friendRequestService.sendInvite(user9, user11);
        //10 y 2
        User user10 = usersService.getUserByEmail("user10@email.com");
        friendRequestService.sendInvite(user10, user11);
        User user15 = usersService.getUserByEmail("user15@email.com");
        friendRequestService.sendInvite(user15, user11);
        User user14 = usersService.getUserByEmail("user14@email.com");
        friendRequestService.sendInvite(user14, user11);


        //agregar post a user1
        // Agregar posts para User1
        Post p1 = new Post("Mis vacaciones de verano", LocalDate.of(2023, 7, 15), "¡Acabo de regresar de unas increíbles vacaciones en la playa!", user2);
        p1.setStatus(PostStatusService.getStatus(PostStatusService.Censurada));
        postService.addNewPost(p1);

        Post p2 = new Post("Nuevo libro favorito", LocalDate.of(2023, 8, 5), "¡Acabo de leer un libro fascinante que quiero recomendar a todos!", user2);
        p2.setStatus(PostStatusService.getStatus(PostStatusService.Censurada));

        postService.addNewPost(p2);

        Post p3 = new Post("Experiencia culinaria", LocalDate.of(2023, 9, 20), "Descubrí un nuevo restaurante en la ciudad y probé platos increíbles.", user2);
        p3.setStatus(PostStatusService.getStatus(PostStatusService.Moderada));

        postService.addNewPost(p3);

        Post p4 = new Post("Concierto inolvidable", LocalDate.of(2023, 10, 12), "Asistí a un concierto de mi banda favorita y fue una experiencia asombrosa.", user2);
        p4.setStatus(PostStatusService.getStatus(PostStatusService.Moderada));

        postService.addNewPost(p4);

        Post p5 = new Post("Aventuras al aire libre", LocalDate.of(2023, 11, 8), "Exploré nuevas rutas de senderismo y disfruté del contacto con la naturaleza.", user2);

        postService.addNewPost(p5);

        Post p6 = new Post("Logros personales", LocalDate.of(2024, 1, 3), "Hoy logré alcanzar una meta importante para mí. ¡Me siento increíble!", user2);
        postService.addNewPost(p6);
    // Creación de la segunda publicación
        Post p7 = new Post("Celebrando un hito", LocalDate.of(2024, 2, 15), "Hoy he alcanzado un hito significativo en mi carrera. ¡Celebrando con gratitud!", user2);
        postService.addNewPost(p7);

    // Creación de la tercera publicación
        Post p8 = new Post("Reflexión sobre el progreso", LocalDate.of(2024, 3, 8), "Reflexionando sobre el progreso hecho hasta ahora. Agradecido por el viaje y emocionado por lo que está por venir.", user2);
        postService.addNewPost(p8);

    // Creación de la cuarta publicación
        Post p9 = new Post("Momento de inspiración", LocalDate.of(2024, 4, 21), "Encuentro inspiración en las pequeñas cosas de la vida. Hoy, eso significa detenerme a apreciar la belleza de la naturaleza.", user2);
        postService.addNewPost(p9);

        Post p10user1= new Post("Nuevo descubrimiento", LocalDate.of(2024, 4, 22), "Hoy descubrí algo nuevo sobre mí mismo. A veces, el autodescubrimiento puede ser tan emocionante como explorar un nuevo lugar.", user2);
        postService.addNewPost(p10user1);
        //caso de pureba 42 usuario 13 TIENE 3 PUBLICACIONES 1 DE CADA TIPO
        // Segunda publicación adicional
        Post p10 = new Post("Nuevo descubrimiento", LocalDate.of(2024, 4, 22), "Hoy descubrí algo nuevo sobre mí mismo. A veces, el autodescubrimiento puede ser tan emocionante como explorar un nuevo lugar.", user13);
        p10.setStatus(PostStatusService.getStatus(PostStatusService.Censurada));
        postService.addNewPost(p10);

        Post p11 = new Post("Reflexiones nocturnas", LocalDate.of(2024, 4, 23), "Las noches tranquilas son el momento perfecto para reflexionar sobre el día que pasó y planificar el siguiente.", user13);
        p11.setStatus(PostStatusService.getStatus(PostStatusService.Moderada));
        postService.addNewPost(p11);

        Post p12 = new Post("Aventuras culinarias", LocalDate.of(2024, 4, 24), "Explorando nuevos sabores en la cocina. Hoy me aventuré a preparar un plato exótico y el resultado fue increíble.", user13);
        postService.addNewPost(p12);

        //agregar 10 post a cada usuasrio hasta el 15
        for(int i=1 ;i<=15;i++){
            User user = usersService.getUserByEmail("user"+String.format("%02d", i)+"@email.com");
            if(user!=null && !Objects.equals(user.getEmail(), "user02@email.com")) {
                for(int j=0;j<10;j++) {
                    Post p = new Post("Publicación de prueba"+j, LocalDate.of(2024, 4, 24), "Esta es una publicación de prueba"+j, user);
                    postService.addNewPost(p);
                }
            }
        }
        //agregar las 10 para el admin tb
        User user = usersService.getUserByEmail("admin@email.com");

        for(int j=0;j<10;j++) {
            Post p = new Post("Publicación de prueba"+j, LocalDate.of(2024, 4, 24), "Esta es una publicación de prueba"+j, user);
            postService.addNewPost(p);
        }
    }
}
