const {ObjectId} = require("mongodb");
const {requireRestFriendship} = require('../../middlewares/authentication');
/**
 * toda ruta que empiece por /api/v1.0/chats debe estar autenticada
 */
module.exports = function (app,usersRepository,friendshipRepository,chatsRepository) {
    /**
     * retornara una lista de todos los amigos del usuario autenticado, la lista estara ordenada por nombre
     *
     * @return {Array} lista de amigos con el identificador email nombre y apellidos
     */
    app.get('/api/v1.0/chats/getFriends', async (req, res) => {
        let userId = new ObjectId(res.userId);
        let friendFilter = { user_id: userId };
        let friendsList = [];

        try {
            // Obtener las amistades del usuario
            const friendships = await friendshipRepository.findAllFriendships(friendFilter, {});

            // Mapear las amistades para obtener el ID de los amigos
            let friendIds = friendships.map(friendship => friendship.friend_id);

            // Obtener detalles de los amigos
            const friends = await usersRepository.findUsersByFilter(
                { _id: { $in: friendIds } },
                { projection: { email: 1, name: 1, surname: 1 }, sort: { name: 1, surname: 1 } }
            );

            for (let friend of friends) {
                // Obtener la fecha de la amistad
                let date = friendships.find(f => f.friend_id.equals(friend._id)).date;

                // Obtener el último mensaje de la conversación con el amigo
                let roomName = userId < friend._id ? `${userId}-${friend._id}` : `${friend._id}-${userId}`;
                let lastMessage = await chatsRepository.getLastMessage(roomName);

                let friendData = {
                    _id: friend._id,
                    email: friend.email,
                    name: friend.name,
                    surname: friend.surname,
                    date: `${date.getDate()}-${date.getMonth() + 1}-${date.getFullYear()}`,
                    lastMessage: lastMessage ? {
                        timestamp: lastMessage.timestamp,
                        content: lastMessage.content
                    } : null
                };

                friendsList.push(friendData);
            }

            res.status(200).json({ friends: friendsList });
        } catch (error) {
            console.error("Error al obtener la lista de amigos", error);
            res.status(500).json({ error: "Se produjo un error al obtener la lista de amigos" });
        }
    });
    /**
     * se encarga de loggear al usuarion y proporcinoa un token
     */
    app.post('/api/v1.0/users/login',function(req,res){

        console.log("entra ");
        try{
            let securePass=app.get("crypto").createHmac("sha256",app.get("clave")).
            update(req.body.password).digest('hex');
            let filter={email:req.body.email,password:securePass};

            let options={};
            usersRepository.findUser(filter,options).then(user=>{
                if(user===null){
                    res.status(401);//no autorizado
                    res.json({error:"Usuario o contraseña incorrectos."});
                }else{
                    let token=app.get('jwt').sign(
                        {user:user.email,id:user._id,time:Date.now()/1000},"secreto");

                    res.status(200);
                    res.json({
                        message:"usuario autorizado",
                        authenticated:true,
                        token:token

                    })

                }
            }).catch(error=>{
                res.status(401);
                res.json({error:"Se ha producido un error al veriricar credenciales",authenticated:false});
            });
        }catch (error){
            res.status(500);
            res.json({
                message:"se ha producido un error al verificar credenciales",
                authenticated:false
            });
        }

    });

    /**
     * obtiene todos los chats del usuario autenticado en los que esta participando
     */
    app.get('/api/v1.0/chats/getChats',function (req,res) {

        //en la col chats el id del user y el friend es id

        chatsRepository.getChats(res.userId).then(chats=>{

            res.status(200);
            //ahora para chat que es el objeto de mongo vas
            res.json(chats);
        }).catch(error=>{
            res.status(500);
            res.json({error:"Se ha producido un error al buscar los chats"});
        });
    });

    /**
     * Envía un mensaje a otro usuario
     */
    app.post('/api/v1.0/chats/sendMessage/:otherId', requireRestFriendship(friendshipRepository), async function (req, res) {
        const userId = new ObjectId(res.userId);
        const otherId = new ObjectId(req.params.otherId);
        const message = req.body.message;

        if (!message || message.trim().length === 0) {
            res.status(400).json({ error: "El mensaje no puede estar vacío." });
            return;
        }

        try {
            const roomName = userId < otherId ? `${userId}-${otherId}` : `${otherId}-${userId}`;
            console.log("Esto es el nroomName " +roomName);
            const result = await chatsRepository.addMessage(userId, otherId, message, roomName);

            res.status(200).json({ success: "Mensaje enviado.", result });
        } catch (error) {
            console.error("Error al enviar mensaje:", error);
            res.status(500).json({ error: "Hubo un error al enviar el mensaje." });
        }
    });

    /**
     * obtiene los mensajes de un chat del usuario autenticado dado su id
     */
    app.get('/api/v1.0/chats/getMessages/:conversationId',function (req,res) {

        let id= new ObjectId(req.params.conversationId);



        chatsRepository.getChat(res.userId, id).then(chat=>{
            if (chat == null) {
                res.status(404).json({error: 'No encontrado'});
                return;
            }
            if(chat.userId.toString()!==res.userId && chat.friendId.toString()!==res.userId){
                res.status(400).json({error:"No puedes ver los mensajes de este chat no pterences ael "});
                return;
            }
            if(chat){
                res.status(200);
                res.json(chat.messages); //NO SE SI ESTA BIEN
            }else{
                res.status(404).json({ error: "El chat no fue encontrado" });
            }
        }).catch(error=>{
            res.status(500);
            res.json({error:"Se ha producido un error al buscar el chat"});
        });
    });

    /**
     * Elimina una conversación dado su id (se borra para los dos usuarios que participan en ella)
     */
    app.delete('/api/v1.0/chats/deleteChat/:conversationId',function (req, res) {
        const userId = res.userId;
        const conversationId = new ObjectId( req.params.conversationId);

        chatsRepository.deleteChat(userId, conversationId)
            .then(result => {
                if (result.success) {
                    res.status(200).json({ message: "El chat ha sido eliminado exitosamente" });
                } else {
                    res.status(404).json({ error: result.error });
                }
            })
            .catch(error => {
                res.status(500).json({ error: "Se ha producido un error al eliminar el chat" });
            });
    });

    /**
     * Marca un mensaje como leído dado su id
     */
    app.put('/api/v1.0/chats/markAsRead/:conversationId/:messageId',function (req, res) {
        const userId = res.userId;
        const conversationId = new ObjectId(req.params.conversationId);
        const messageId = new ObjectId(req.params.messageId);

        //comprobar que estes enla conversacion para marcar como leido

        chatsRepository.markAsRead(conversationId,messageId,userId)
            .then(result => {
                if (result.success) {
                    res.status(200).json({ message: "Los mensajes han sido marcados como leídos" });
                } else {
                    res.status(404).json({ error: result.error });
                }
            })
            .catch(error => {
                res.status(500).json({ error: "Se ha producido un error al marcar los mensajes como leídos" });
            });
    });

    /**
     * Devuelve un usuario dado su id
     */
    app.get('/api/v1.0/users/getUserById/:userId', function (req, res) {
        const userId = new ObjectId(req.params.userId);

        usersRepository.findUser({ _id: userId })
            .then(user => {
                if (user) {
                    res.status(200).json({
                        email: user.email,
                        name: user.name,
                        surname: user.surname,
                    });
                } else {
                    res.status(404).json({ error: "Usuario no encontrado" });
                }
            })
            .catch(error => {
                console.error("Error al obtener detalles del usuario:", error);
                res.status(500).json({ error: "Error al obtener detalles del usuario." });
            });
    });

    /**
     * Crea un chat
     */
    app.post("/api/v1.0/chats/createChat", (req, res) => {
        const userId = new ObjectId(res.userId); // ID del usuario autenticado
        const friendId = new ObjectId(req.body.friendId); // ID del amigo
        console.log(userId);
        if (!friendId) {
            return res.status(400).json({ error: "ID del amigo es requerido." });
        }

        // Verificar si ya existe un chat entre los dos usuarios
        chatsRepository.getChat(userId, friendId).then(existingChat => {
            if (existingChat) {
                // Si el chat ya existe, retornar error o el chat existente
                res.status(409).json({ error: "El chat ya existe." });
            } else {
                // Crear un nuevo chat si no existe
                const newChat = {
                    userId,
                    friendId,
                    messages: [],
                    createdAt: new Date(),
                    roomName: userId < friendId ? `${userId}-${friendId}` : `${friendId}-${userId}`
                };

                chatsRepository.insertChat(newChat).then(result => {
                    res.status(201).json(result);
                }).catch(error => {
                    console.error("Error al crear el chat:", error);
                    res.status(500).json({ error: "Error al crear el chat." });
                });
            }
        }).catch(error => {
            console.error("Error al buscar el chat:", error);
            res.status(500).json({ error: "Error al buscar el chat." });
        });
    });

    //obtener los mnesaajes no leidos de una conversa<cion
    app.get('/api/v1.0/chats/getUnreadMessages/:userId/:friendId',function (req,res) {
        const userId = new ObjectId(req.params.userId);
        const friendID = new ObjectId(req.params.friendId);

        chatsRepository.getUnreadMessages(userId, friendID)
            .then(messagesCount => {
                res.status(200).json(messagesCount);
            })
            .catch(error => {
                console.error("Error al obtener mensajes no leídos:", error);
                res.status(500).json({ error: "Error al obtener mensajes no leídos." });
            });
    });
}