const {ObjectId} = require("mongodb");
const {requireAuth} = require("../middlewares/authentication");

module.exports = function (app,userRepository,postRepository){

    /**
     * Muestra la vista para añadir una nueva publicación
     */
    app.get("/post/add",requireAuth, function (req,res) {
        res.render("post/add.twig");
    })

    /**
     * Creación de la publicación
     */
    app.post("/post/add",requireAuth, async function (req,res) {
        const {title , content } = req.body;

        if(!title || !content) {
            req.flash("error",["Todos lo campos son obligatorios."]);
            return res.redirect("/post/add");
        }
        const newPost = {
            user_id: new ObjectId(req.session.user._id),
            title,
            content,
            created_at: new Date(),
        }
            postRepository.insertPost(newPost).then(postId => {
                req.flash("success",["Publicación creada exitosamente."]);
                res.redirect("/posts/my-posts");
            }).catch (error => {
            console.error("Error al crear la publicación: ", error);
            req.flash("error",["Error al crear la publiación."]);
            res.redirect("/post/add");
        });
    });

    /**
     * Muestra las publicaciones de el usuario en sesión
     */
    app.get("/posts/my-posts",requireAuth,async (req,res) => {
        let page = parseInt(req.query.page) || 1;
        let limit = app.get("maxSizePerPage") || 5;
        let filter = { user_id: new ObjectId(req.session.user._id)};
        try{
            let posts = await postRepository.findPostsPaginated(filter, page, limit);
            let totalPosts = await postRepository.count(filter);
            let pageCount = Math.ceil(totalPosts/limit);
            res.render("post/list.twig", {
                posts: posts,
                currentPage: page,
                pageCount: pageCount,
                itemCount: totalPosts,
                pages: app.get('paginate').getArrayPages(req)(3, pageCount, page)
            });
        } catch(error) {
            console.error("Error al obtener publicaciones:", error);
            req.flash("error", "Error al cargar las publicaciones.");
            res.redirect("/users/login");
        }
    })
}