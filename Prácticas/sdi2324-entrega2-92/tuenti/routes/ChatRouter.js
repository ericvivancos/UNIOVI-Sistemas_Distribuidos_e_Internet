/*
se encagra de los chats


 */
const {ObjectId} = require("mongodb");
const express = require("express");
const path = require("path");
//const {errorStrictEqual} = require("mongodb/lib/core/utils");
module.exports = function (app, friendShipRepository, chatRepository,userRepository) {
    //sino te sirve el js como un archivo y entra 2 veces al endterpoiunt
    app.use(express.static(path.join(__dirname, 'views', 'chats')));

    /**
     * Muestra el chat entre un usuario y un amigo.
     */
    app.get('/chat/:userId/:friendId', function (req, res) {
        const userId = req.params.userId;

        const friendId = req.params.friendId;
        //pasarlo a ObjectId
        let idAmigo= new ObjectId(friendId);

        //obtener el id del usuario actual en session par poder sacar su conver con su amigo
        if(req.session.user===undefined||req.session.user===null){
            let err = [];
            err.push('inicia session para chatear');
            req.flash('error', err);
            res.redirect('users/login');
        }
        let idUsuario= new ObjectId(userId);


        // Aquí puedes buscar en la base de datos la conversación entre el usuario actual y el amigo
        // y luego pasarla a la vista del chat

        chatRepository.getMessages(idUsuario, idAmigo).then(messages => {

            //obtener el nombnre del amigo para el titulo
            userRepository.findUser({_id: idAmigo}).then(friend=> {
                res.render('chats/chat.twig', {messages: messages, friendId: friendId, userId: userId,friendName:friend.name});
            }).catch(error => {
                //en caso de error mostrar solo chat online sin nombre
                res.render('chats/chat.twig', {messages: messages, friendId: friendId, userId: userId});

            });
        }).catch(error => {
            let err = [];
            err.push('Error al cargar los mensajes del chat');
            err.push(error);
            req.flash('error', err);
        });
    });

    //endpoint que actualiza los mensajes
    /**
     * A ELIMINAER AL HACER LA API YA QUE LA VISTA LLAMARIA A LA API
     *
     */
    app.get('/chat/:userId/:friendId/messages', function (req, res) {
        const userId = req.params.userId;
        const friendId = req.params.friendId;
        let idAmigo= new ObjectId(friendId);
        let idUsuario= new ObjectId(userId);
        chatRepository.getMessages(idUsuario, idAmigo).then(messages => {
            console.log('mensajes que se envian a al vista ',messages);
            res.json(messages);
        }).catch(error => {
            let err = [];
            err.push('Error al cargar los mensajes del chat');
            err.push(error);
            req.flash('error', err);
        });
    });
}