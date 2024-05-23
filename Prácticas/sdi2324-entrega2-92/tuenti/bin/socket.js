// socket.js

// Importar las dependencias necesarias
const socketIo = require('socket.io'); // Socket.io para la comunicación en tiempo real
const axios = require('axios'); // Axios para hacer solicitudes HTTP
const ChatRepository = require('../repositories/ChatRepository'); // Repositorio para manejo de chats
// Se define una conexión de base de datos de MongoDB
const { MongoClient } = require("mongodb");
const connectionStrings = 'mongodb://localhost:27017/';
const dbClient = new MongoClient(connectionStrings);

// Exportar una función que inicializa el socket.io con el servidor y la aplicación de Express
module.exports = function (server, app) {
    // Crear una instancia de socket.io con el servidor proporcionado
    const io = socketIo(server);

    // Crear una instancia del repositorio de chat con la aplicación y la base de datos
    let chatRepository = new ChatRepository(app, app.get('db'));

    // Escuchar el evento de conexión cuando un cliente se conecta al servidor de sockets
    io.on('connection', (socket) => {
        console.log('Un usuario se ha conectado'); // Mensaje en consola cuando un usuario se conecta

        const token = socket.handshake.query.token; // Obtener el token desde la consulta de la conexión

        // Cuando un usuario se une a una sala de chat
        socket.on('joinRoom', ({ user1Id, user2Id }) => {
            // Determinar el nombre de la sala según el ID de los usuarios
            const roomName = user1Id < user2Id ? `${user1Id}-${user2Id}` : `${user2Id}-${user1Id}`;
            socket.join(roomName); // Unir al socket a la sala determinada
            console.log('Un usuario se ha unido a la sala de chat', roomName); // Notificación de unión a la sala
            io.to(roomName).emit('chatUpdated'); // Notificar a la sala que el chat se ha actualizado
        });

        // Escuchar el evento de mensaje de chat
        socket.on('chat message', (msg, roomName) => {
            io.to(roomName).emit('chat message', msg); // Enviar el mensaje a todos en la sala
        });

        // Evento de desconexión
        socket.on('disconnect', () => {
            console.log('Un usuario se ha desconectado'); // Mensaje en consola cuando un usuario se desconecta
        });

        // Evento para recibir y procesar un mensaje de chat
        socket.on('chat-message', async (data) => {
            console.log('Mensaje recibido en el servidor:', data.user1Id, data.user2Id, data.message);

            // Determinar el nombre de la sala de chat
            const roomName = data.user1Id < data.user2Id ? `${data.user1Id}-${data.user2Id}` : `${data.user2Id}-${data.user1Id}`;

            // URL del endpoint al cual se le enviará el mensaje
            const url = `http://localhost:8081/api/v1.0/chats/sendMessage/${data.user2Id}`;

            try {
                // Enviar el mensaje a través de una solicitud HTTP
                const result = await axios.post(
                    url,
                    {
                        message: data.message, // Contenido del mensaje
                        userId: data.user1Id, // ID del usuario que envía el mensaje
                    },
                    {
                        headers: {
                            'Content-Type': 'application/json',
                            'token': token, // Incluir el token para autenticación
                        },
                    }
                );

                // Notificar a la sala que el chat se ha actualizado
                console.log('Mensaje enviado, ahora se enviará a la sala de chat');
                io.in(roomName).emit('chatUpdated'); // Evento para actualizar el chat en los clientes


            } catch (error) {
                console.error('Error al agregar el mensaje al chat:', error); // Manejo de errores
                console.error('Detalles:', error.response?.data); // Detalles adicionales del error
            }
        });
    });

    return io; // Devolver la instancia de socket.io
};
