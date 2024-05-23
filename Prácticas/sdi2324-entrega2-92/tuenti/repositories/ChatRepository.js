// tuenti/repositories/ChatRepository.js

// Importar las dependencias necesarias
const { ObjectId } = require("mongodb"); // Utilizado para trabajar con identificadores de MongoDB

/**
 * Representa un repositorio para almacenar mensajes de chat entre usuarios.
 */
class ChatRepository {
    /**
     * Crea una instancia de ChatRepository.
     * @param {Object} app - El objeto de la aplicación.
     * @param {Object} dbClient - El objeto del cliente de la base de datos.
     */
    constructor(app, dbClient) {
        this.app = app; // Guardar la referencia a la aplicación
        this.dbClient = dbClient; // Guardar el cliente de la base de datos
        this.init(); // Llamar a la función de inicialización
    }

    /**
     * Inicializa la conexión de la base de datos y configura la colección.
     */
    init() {
        this.dbClient.connect(); // Conectar con la base de datos
        this.db = this.dbClient.db('tuenti'); // Establecer la base de datos a utilizar
        this.collection = this.db.collection('chats'); // Establecer la colección para chats
    }

    /**
     * Marca un mensaje como leído en un chat.
     * @param {string} chatId - El ID del chat.
     * @param {string} userId - El ID del usuario.
     * @param {string} messageId - El ID del mensaje.
     * @returns {Object} - Resultado de la operación.
     */
    async markAsRead(chatId, messageId, userId) {
        // Buscar el chat por su ID
        const chat = await this.collection.findOne({ _id: new ObjectId(chatId) });

        if (chat) {
            // Comprobar si el usuario es parte del chat
            if (
                chat.userId.toString() !== userId.toString() &&
                chat.friendId.toString() !== userId.toString()
            ) {
                return {
                    success: false,
                    error: "No puedes realizar esa acción, no eres parte del chat",
                };
            }

            // Encontrar el mensaje y marcarlo como leído
            const messageIndex = chat.messages.findIndex(
                (msg) => msg._id.toString() === messageId.toString()
            );
            if (messageIndex !== -1) {
                chat.messages[messageIndex].read = true; // Marcar como leído

                // Actualizar los mensajes en la base de datos
                const result = await this.collection.updateOne(
                    { _id: chat._id },
                    { $set: { messages: chat.messages } }
                );

                if (result.modifiedCount === 1) {
                    return { success: true }; // Operación exitosa
                } else {
                    return {
                        success: false,
                        error: "No se pudo marcar el mensaje como leído",
                    }; // Error en la actualización
                }
            }
            return null; // Si no se encontró el mensaje
        }
    }

    /**
     * Añade un nuevo mensaje a un chat.
     * @param {string} user1Id - El ID del primer usuario.
     * @param {string} user2Id - El ID del segundo usuario.
     * @param {string} message - El contenido del mensaje.
     * @param {string} roomName - El nombre de la sala de chat.
     * @returns {Object} - El chat actualizado o un nuevo chat.
     */
    async addMessage(user1Id, user2Id, message, roomName) {
        // Validar el mensaje
        if (!message || message.trim().length === 0) {
            throw new Error("El mensaje no puede estar vacío.");
        }

        // Crear el objeto del mensaje
        const messageObject = {
            senderId: new ObjectId(user1Id.toString()), // ID del remitente
            content: message.trim(), // Contenido del mensaje
            timestamp: new Date(), // Fecha y hora del mensaje
            read: false, // El mensaje no está leído inicialmente
            _id: new ObjectId(), // ID único para el mensaje
        };

        try {
            // Comprobar si ya existe un chat para estos usuarios
            const existingChat = await this.collection.findOne({
                $or: [
                    { userId: new ObjectId(user1Id.toString()), friendId: new ObjectId(user2Id.toString()) },
                    { userId: new ObjectId(user2Id.toString()), friendId: new ObjectId(user1Id.toString()) },
                ],
            });

            if (existingChat) {
                // Si el chat ya existe, agregar el nuevo mensaje
                existingChat.messages.push(messageObject);

                await this.collection.updateOne(
                    { _id: existingChat._id },
                    { $set: { messages: existingChat.messages } }
                );

                return { chatId: existingChat._id, message: messageObject }; // Devolver el ID del chat y el nuevo mensaje
            } else {
                // Si el chat no existe, crear un nuevo chat con el mensaje
                const newChat = {
                    userId: new ObjectId(user1Id.toString()), // ID del primer usuario
                    friendId: new ObjectId(user2Id.toString()), // ID del segundo usuario
                    messages: [messageObject], // Lista de mensajes, comenzando con el nuevo mensaje
                    roomName: roomName, // Nombre de la sala de chat
                };

                const result = await this.collection.insertOne(newChat); // Insertar el nuevo chat
                return { chatId: result.insertedId, message: messageObject }; // Devolver el ID del nuevo chat y el mensaje
            }
        } catch (error) {
            console.error("Error al insertar el nuevo chat:", error); // Manejo de errores
            throw new Error("Se produjo un error al agregar el mensaje."); // Lanzar error para el manejo adecuado
        }
    }

    /**
     * Obtiene los mensajes entre dos usuarios.
     * @param {string} user1Id - El ID del primer usuario.
     * @param {string} user2Id - El ID del segundo usuario.
     * @returns {Array} - Lista de mensajes en el chat.
     */
    async getMessages(user1Id, user2Id) {
        // Buscar el chat entre dos usuarios
        const chat = await this.collection.findOne({
            $or: [
                { userId: new ObjectId(user1Id.toString()), friendId: new ObjectId(user2Id.toString()) },
                { userId: new ObjectId(user2Id.toString()), friendId: new ObjectId(user1Id.toString()) },
            ],
        });

        return chat ? chat.messages : []; // Devolver la lista de mensajes o una lista vacía
    }

    /**
     * Obtiene todos los chats de un usuario.
     * @param {string} userId - El ID del usuario.
     * @returns {Array} - Lista de chats del usuario.
     */
    async getChats(userId) {
        const chats = await this.collection.find(

            { userId: new ObjectId(userId.toString()) }
        ).toArray();
        const chats2 = await this.collection.find(

            { friendId: new ObjectId(userId.toString()) }
        ).toArray();

        return {sender:chats,reciver:chats2};
    }

    /**
     * Obtiene un chat específico por su ID y el ID del usuario.
     * @param {string} userId - El ID del usuario.
     * @param {string} conversationId - El ID de la conversación.
     * @returns {Object} - El chat correspondiente.
     */
    async getChat(userId, conversationId) {
        const chat = await this.collection.findOne({
            $and: [
                {
                    $or: [
                        { userId: new ObjectId(userId.toString()) }, // Si el usuario es parte del chat
                        { friendId: new ObjectId(userId.toString()) },
                    ],
                },
                { _id: new ObjectId(conversationId.toString()) }, // El ID de la conversación
            ],
        });

        return chat; // Devolver el chat o null si no se encuentra
    }

    /**
     * Elimina un chat por su ID y el ID del usuario.
     * @param {string} userId - El ID del usuario.
     * @param {string} conversationId - El ID del chat.
     * @returns {Object} - Resultado de la operación.
     */
    async deleteChat(userId, conversationId) {
        try {
            const result = await this.collection.deleteOne({
                $and: [
                    {
                        $or: [
                            { userId: new ObjectId(userId.toString()) }, // Donde el usuario sea parte del chat
                            { friendId: new ObjectId(userId.toString()) },
                        ],
                    },
                    { _id: new ObjectId(conversationId.toString()) }, // ID del chat a eliminar
                ],
            });

            if (result.deletedCount === 1) {
                return { success: true }; // Operación exitosa
            } else {
                return { success: false, error: "El chat no fue encontrado" }; // Si no se encontró el chat
            }
        } catch (error) {
            return { success: false, error: "Se ha producido un error al eliminar el chat" }; // Manejo de errores
        }
    }

    /**
     * Obtiene el último mensaje de un chat por su nombre de sala.
     * @param {string} roomName - El nombre de la sala de chat.
     * @returns {Object} - El último mensaje del chat o null si no hay mensajes.
     */
    async getLastMessage(roomName) {
        const chat = await this.collection.findOne({ roomName: roomName }); // Buscar el chat por su nombre de sala
        if (chat && chat.messages.length > 0) {
            return chat.messages[chat.messages.length - 1]; // Devolver el último mensaje
        }
        return null;
    }

        //busca la conver y obtiene los no leidos no manddaos por el userID
    async getUnreadMessages(userID, friendID) {
        const messages= await this.getMessages(userID, friendID);

            const value= messages.filter(message => message.senderId.toString() !== userID.toString() && message.read===false);
            console.log('mensajes no leídos obtenidos ', value.length);
            return value.length; // Devuelve el número de mensajes no leídos

    }

    /**
     * Inserta un nuevo chat en la base de datos.
     * @param {Object} chat - El chat a insertar.
     * @returns {Object} - El chat insertado.
     */
    async insertChat(chat) {
        try {
            const result = await this.collection.insertOne(chat); // Insertar el chat
            const insertedChat = await this.collection.findOne({ _id: result.insertedId }); // Buscar el chat insertado
            return insertedChat; // Devolver el chat insertado
        } catch (error) {
            console.error("Error al insertar el chat:", error); // Manejo de errores
            throw new Error("Error al insertar el chat"); // Lanzar un error
        }
    }
}

// Exportar la clase ChatRepository para su uso en otros lugares
module.exports = ChatRepository;
