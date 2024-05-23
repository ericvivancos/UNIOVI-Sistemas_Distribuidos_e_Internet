const {ObjectId, Timestamp} = require("mongodb");
const {requireAuth} = require("../middlewares/authentication");

module.exports = function (app, userRepository, friendRequestRepository) {

    const status = ["pending", "accepted"]

    /**
     * Crea una solicitud de amistad con el Id del usaurio en sesión(sender) y el de la posible amistad(receiver)
     */
    app.get("/invite/send/:_id",requireAuth, function (req, res){
        let filter = {receiver_id: new ObjectId(req.params._id),
            sender_id: new ObjectId(req.session.user._id)}
        friendRequestRepository.findFriendRequest(filter, {}).then(friendRequest => {
            if (friendRequest!==null && friendRequest!==undefined){
                let error = []
                error.push("Ya existe una petición de amistad pendiente");
                req.flash('error', error);
                res.redirect("/listUsersSocialMedia");
            }
            else{
                let newFriendRequest = {
                    date: new Date(),
                    status: status[0],
                    receiver_id: filter.receiver_id,
                    sender_id: filter.sender_id
                }
                friendRequestRepository.insertFriendRequest(newFriendRequest).then(userId => {
                    let suc = []
                    suc.push("Solicitud enviada")
                    req.flash('success', suc);
                    res.redirect("/listUsersSocialMedia");
                }).catch(error => {
                    console.log("Error inesperado insertando una petición de amistad");
                })
            }
        }).catch(error => {
            console.log("Error inesperado insertando buscando la petición de amistad");
        })
    });

    /**
     * Muestra la lista paginada de solicitudes de amistad pendientes del usuario en sesión
     */
    app.get("/friendRequest/list", function (req, res) {
        if (req.query.page === undefined || req.query.page === null) {
            req.query.page = 1;
        }
        let limit = app.get('maxSizePerPage');

        let filter = {
            receiver_id: new ObjectId(req.session.user._id),
            status: status[0]
        };

        let list = [];

        friendRequestRepository.findAllFriendshipsRequests(filter, {}).then(async (friendRequests) => {
            for (let data of friendRequests) {
                try {
                    let sender = await userRepository.findUser({_id: new ObjectId(data.sender_id)}, {});
                    let fr = {
                        _id: data._id,
                        name: sender.name + " " + sender.surname,
                        email: sender.email,
                        date: `${data.date.getDate()}-${data.date.getMonth() + 1}-${data.date.getFullYear()}`
                    };
                    list.push(fr);
                } catch (error) {
                    console.log("Error inesperado buscando el usuario que envió la petición:", error);
                }
            }
            let pageCount = Math.ceil(list.length / limit);
            let skip = (req.query.page - 1) * limit;
            // Paginar el array list
            let listaPaginada = list.slice(skip, skip + limit);

            // Aquí puedes pasar 'listaPaginada' a tu vista para su renderización
            res.render("friendRequest/list.twig", {
                friendRequests: listaPaginada,
                pageCount: pageCount,
                itemCount: list.length,
                pages: app.get('paginate').getArrayPages(req)(3, pageCount, req.query.page)
            })
        }).catch(error => {
            console.log("Error inesperado al buscar todas las peticiones de amistad:", error);
        });
    })

    /**
     * Tramita la solicitud de amistad pasandola a estado aceptada para luego pasarle el control
     * de la creación de la amistad al router Frienship
     */
    app.get('/friendRequest/accept/:_id', function (req, res){
        let filter = {_id: new ObjectId(req.params._id)};
        friendRequestRepository.updateFriendRequest(filter, {
            status: status[1]
        }).then(() =>{
            friendRequestRepository.findFriendRequest(filter, {}).then(friendRequest => {
                res.redirect("/friendship/accepted/"+friendRequest.sender_id+"/"+friendRequest.receiver_id);
            })
        }).catch(err => {
            console.log("Error al aceptar la solicutd: ", err)
        });
    })

}