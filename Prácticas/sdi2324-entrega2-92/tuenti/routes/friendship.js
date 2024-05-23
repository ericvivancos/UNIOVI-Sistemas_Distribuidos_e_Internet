const {ObjectId} = require("mongodb");
const {requireAuth,requireFriendship} = require("../middlewares/authentication");
module.exports = function (app, friendshipRepository, userRepository,postRepository) {

    /**
     * Se crea la amistad entre los dos usuarios
     */
    app.get("/friendship/accepted/:sender_id/:receiver_id", requireAuth, function (req, res) {
        let friendship = {
            date: new Date(),
            friend_id: new ObjectId(req.params.sender_id),
            user_id: new ObjectId(req.params.receiver_id)
        }
        friendshipRepository.insertFriendship(friendship).then(() => {
            friendshipRepository.insertFriendship({
                date: friendship.date,
                friend_id: friendship.user_id,
                user_id: friendship.friend_id
            }).then(() => {
                let suc = []
                suc.push("Amistad acceptada")
                req.flash('success', suc);
                res.redirect("/friendship/list");
            })
        }).catch(error => {
            console.log("Error al crear la amistad ", error);
        })
    })

    /**
     * Se muestra la lista paginada de amigos del usuario en sesión
     */
    app.get("/friendship/list", async function(req, res) {
        try {
            //let limit = app.get('maxSizePerPage') || 5;
           // let page = parseInt(req.query.page) || 1;
           // console.log(page);
            if (req.query.page === undefined || req.query.page === null) {
                req.query.page = 1;
            }
            let limit = app.get('maxSizePerPage');
            //let skip = (page - 1) * limit;
            let filter = {user_id: new ObjectId(req.session.user._id)}
            let friendships = await friendshipRepository.findAllFriendships(filter, {});
            if (!Array.isArray(friendships)) {
                throw new Error("El resultado no es un array");
            }
            let list = [];
            // Obtengo la información adicional para cada amistad
            for (let friendship of friendships) {
                let friend = await userRepository.findUser(
                    {_id: friendship.friend_id},
                    {}
                );
                console.log(friend._id);
                // TODO:
                let lastPost = await postRepository.findLastPost(
                { user_id: friend._id},
                {}
                );
                list.push({
                    email: friend.email,
                    name: friend.name ,
                    surname: friend.surname,
                    friendshipDate: `${friendship.date.getDate()}-${friendship.date.getMonth() + 1}-${friendship.date.getFullYear()}`,
                    lastPost: lastPost ? lastPost.title : "No hay publicaciones",
                    _id: friend._id
                });


            }

            //Calculamos el número total de páginas
            //let totalFriendships = await friendshipRepository.count(filter);
           // let pageCount = Math.ceil(totalFriendships / limit);
            let pageCount = Math.ceil(list.length / limit);
            let skip = (req.query.page - 1) * limit;
            let listaPaginada = list.slice(skip, skip + limit);

            res.render("friendship/list.twig", {
                friendships: listaPaginada,
                currentPage: parseInt(req.query.page),
                pageCount: pageCount,
                itemCount: list.length,
                pages: app.get("paginate").getArrayPages(req)(3, pageCount, req.query.page),
                userId: req.session.user._id
            });
        } catch (error) {
            console.error("Error al buscar amistades: ", error);
            req.flash("error", "Error al cargar la lista de amistades");
            res.redirect("/error");
        }


    });

    /**
     * Muestra el perfil de un usuario con sus posts.
     */
    app.get("/profile/:id", requireAuth, requireFriendship(friendshipRepository), async function (req, res) {
        let friendId = req.params.id;
        let limit = app.get("maxSizePerPage");
        let page = parseInt(req.query.page) || 1;

        // Buscar al amigo por su ID
        try {
            let friend = await userRepository.findUser({ _id: new ObjectId(friendId) }, {});
            if (!friend) {
                req.flash("error", "No se encontró el perfil del amigo.");
                return res.redirect("/listUsersSocialMedia"); // o donde quieras redirigir
            }

            // Buscar publicaciones del amigo
            let filter = { user_id: new ObjectId(friendId) };
            let options = { sort: { created_at: -1 } };

            let posts = await postRepository.findPostsPaginated(filter, page, limit);
            let totalPosts = await postRepository.count(filter);

            let pageCount = Math.ceil(totalPosts / limit);
            let response = {
                friend: {
                    email: friend.email,
                    name: friend.name,
                    surname: friend.surname,
                },
                posts,
                currentPage: page,
                pages: app.get("paginate").getArrayPages(req)(3, pageCount, page),
            };

            res.render("user/profile.twig", response);
        } catch (error) {
            console.error("Error al obtener el perfil y publicaciones:", error);
            req.flash("error", ["Hubo un problema al cargar el perfil."]);
            res.redirect("/listUsersSocialMedia");
        }
    });

}