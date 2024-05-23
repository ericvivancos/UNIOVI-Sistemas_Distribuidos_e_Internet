    package com.example.sdi2324entrega192.services;

    import com.example.sdi2324entrega192.entities.FriendRequest;
    import com.example.sdi2324entrega192.entities.Friendship;
    import com.example.sdi2324entrega192.entities.Post;
    import com.example.sdi2324entrega192.entities.User;
    import com.example.sdi2324entrega192.repositories.FriendRequestRepository;
    import org.springframework.stereotype.Service;
    import java.sql.Date;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.Month;
    import javax.persistence.EntityManager;


    import javax.annotation.PostConstruct;
    import javax.transaction.Transactional;

    @Service
    public class InsertSampleDataService {
        private final UsersService usersService;
        private final RolesService rolesService;
        private final  FriendshipService friendshipService; // Agrega el servicio de amistad
        private final FriendshipInitializationService friendshipInitializationService;
        private final PostService postService;

        public InsertSampleDataService(UsersService usersService, RolesService rolesService, FriendRequestService friendRequestService, FriendshipService friendshipService, FriendshipInitializationService friendshipInitializationService, PostService postService){
            this.usersService =usersService;
            this.rolesService = rolesService;
            this.friendshipInitializationService = friendshipInitializationService;

            this.friendshipService = friendshipService;
            this.postService = postService;
        }
        @PostConstruct
        @Transactional

        public void init() {

            User user = new User("uo303984@uniovi.es", "Eric", "Vivancos");
            user.setPassword("123456");
            user.setRole(rolesService.getRoles()[0]);

            User user1 = new User("user01@email.com", "pedro", "alvarez");
            user1.setPassword("Us3r@1-PASSW");
            user1.setRole(rolesService.getRoles()[0]);

            User user2 = new User("user02@email.com", "juan", "Diaz");
            user2.setPassword("Us3r@2-PASSW");
            user2.setRole(rolesService.getRoles()[0]);



            User user3 = new User("user03@email.com", "ana", "Gomez");
            user3.setPassword("Us3r@3-PASSW");
            user3.setRole(rolesService.getRoles()[0]);

            User user4 = new User("user04@email.com", "luis", "Hernandez");
            user4.setPassword("Us3r@4-PASSW");
            user4.setRole(rolesService.getRoles()[0]);



            User user5 = new User("user05@email.com", "maria", "Rodriguez");
            user5.setPassword("Us3r@5-PASSW");
            user5.setRole(rolesService.getRoles()[0]);

            User user6 = new User("user06@email.com", "carlos", "Martinez");
            user6.setPassword("Us3r@6-PASSW");
            user6.setRole(rolesService.getRoles()[0]);

            User user7 = new User("user07@email.com", "laura", "Lopez");
            user7.setPassword("Us3r@7-PASSW");
            user7.setRole(rolesService.getRoles()[0]);

            User user8 = new User("user08@email.com", "sergio", "Sanchez");
            user8.setPassword("Us3r@8-PASSW");
            user8.setRole(rolesService.getRoles()[0]);

            User user9 = new User("user09@email.com", "rosa", "Fernandez");
            user9.setPassword("Us3r@9-PASSW");
            user9.setRole(rolesService.getRoles()[0]);

            User user10 = new User("user10@email.com", "pablo", "Ramirez");
            user10.setPassword("Us3r@10-PASSW");
            user10.setRole(rolesService.getRoles()[0]);

            User user11 = new User("user11@email.com", "elena", "Garcia");
            user11.setPassword("Us3r@11-PASSW");
            user11.setRole(rolesService.getRoles()[0]);

            User user12 = new User("user12@email.com", "miguel", "Torres");
            user12.setPassword("Us3r@12-PASSW");
            user12.setRole(rolesService.getRoles()[0]);

            User user13 = new User("user13@email.com", "isabel", "Morales");
            user13.setPassword("Us3r@13-PASSW");
            user13.setRole(rolesService.getRoles()[0]);

            User user14 = new User("user14@email.com", "javier", "Serrano");
            user14.setPassword("Us3r@14-PASSW");
            user14.setRole(rolesService.getRoles()[0]);

            User user15 = new User("user15@email.com", "patricia", "Ortega");
            user15.setPassword("Us3r@15-PASSW");
            user15.setRole(rolesService.getRoles()[0]);


            User admin = new User("admin@email.com", "Administrador", "Prueba");
                admin.setPassword("@Dm1n1str@D0r");

            admin.setRole(rolesService.getRoles()[1]);


            User user16 = new User("user16@email.com", "subirFotos", "prueba42");
            user16.setPassword("Us3r@16-PASSW");
            user16.setRole(rolesService.getRoles()[0]);

            //agregar la soicitud de amistad



            usersService.addUser(user);

            usersService.addUser(admin);
            usersService.addUser(user1);
            usersService.addUser(user2);
            usersService.addUser(user3);
            usersService.addUser(user4);
            usersService.addUser(user5);
            usersService.addUser(user6);
            usersService.addUser(user7);
            usersService.addUser(user8);
            usersService.addUser(user9);
            usersService.addUser(user10);
            usersService.addUser(user11);
            usersService.addUser(user12);
            usersService.addUser(user13);
            usersService.addUser(user14);
            usersService.addUser(user15);
            usersService.addUser(user16);

            // Agregar amistad entre user1 y user2
            friendshipService.createFriendship(user1, user2);


            // Agregar amistad entre user1 y user3
            friendshipService.createFriendship(user1, user3);


            // Agregar amistad entre user1 y user4
            friendshipService.createFriendship(user1, user4);


            // Agregar amistad entre user1 y user5
            friendshipService.createFriendship(user1, user5);


            // Agregar amistad entre user1 y user6
            friendshipService.createFriendship(user1, user6);


            // Agregar amistad entre user1 y user7
            friendshipService.createFriendship(user1, user7);



            //para el test agregar un caso donde solo tengas a 1 y comprobar que tiene ese
            // Agregar amistad entre user2 y user8
            Friendship friendship7 = new Friendship();
            friendship7.setUser(user2);
            friendship7.setFriend(user8);
            LocalDate fechaEspecifica = LocalDate.of(2002, Month.MAY, 22);
            friendship7.setFriendshipDate(Date.valueOf(fechaEspecifica)); // establecer la fecha específica

            friendshipService.save(friendship7);

            // Inicializar amistades
            friendshipInitializationService.initializeFriendships();







        }


    }