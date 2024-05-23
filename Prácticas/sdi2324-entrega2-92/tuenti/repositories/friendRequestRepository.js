// Importar dependencias necesarias
const { ObjectId } = require("mongodb"); // ObjectId para trabajar con identificadores de MongoDB

/**
 * Repositorio para gestionar solicitudes de amistad en una base de datos MongoDB.
 */
module.exports = {
    mongoClient: null, // Cliente de la base de datos
    app: null, // Aplicación asociada al repositorio
    database: "tuenti", // Nombre de la base de datos
    collectionName: "friend_request", // Nombre de la colección para solicitudes de amistad

    /**
     * Inicializa el repositorio con la aplicación y el cliente de la base de datos.
     * @param {Object} app - Referencia a la aplicación.
     * @param {Object} dbClient - Cliente de la base de datos.
     */
    init: function (app, dbClient) {
        this.dbClient = dbClient; // Establece el cliente de la base de datos
        this.app = app; // Establece la aplicación
    },

    /**
     * Inserta una nueva solicitud de amistad en la base de datos.
     * @param {Object} friendRequest - Objeto que representa la solicitud de amistad.
     * @returns {Promise<ObjectId>} - Devuelve el ID de la solicitud insertada.
     */
    insertFriendRequest: async function (friendRequest) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const friendRequestCollection = database.collection(this.collectionName); // Obtener la colección de solicitudes de amistad
            const result = await friendRequestCollection.insertOne(friendRequest); // Insertar la solicitud
            return result.insertedId; // Devuelve el ID de la solicitud insertada
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Encuentra una solicitud de amistad por un filtro dado.
     * @param {Object} filter - Filtro para buscar la solicitud de amistad.
     * @param {Object} [options] - Opciones adicionales para la búsqueda.
     * @returns {Promise<Object|null>} - Devuelve la solicitud encontrada o null si no se encuentra ninguna.
     */
    findFriendRequest: async function (filter, options) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const friendRequestCollection = database.collection(this.collectionName); // Obtener la colección de solicitudes de amistad
            const friendRequest = await friendRequestCollection.findOne(filter, options); // Buscar la solicitud de amistad
            return friendRequest; // Devuelve la solicitud encontrada
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Encuentra todas las solicitudes de amistad que cumplan con un filtro dado.
     * @param {Object} filter - Filtro para buscar solicitudes.
     * @param {Object} [options] - Opciones adicionales para la búsqueda.
     * @returns {Promise<Array>} - Devuelve un array con todas las solicitudes de amistad encontradas.
     */
    findAllFriendshipsRequests: async function (filter, options) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const friendRequestCollection = database.collection(this.collectionName); // Obtener la colección de solicitudes de amistad
            const friendRequests = await friendRequestCollection.find(filter, options).toArray(); // Buscar todas las solicitudes
            return friendRequests; // Devuelve el array de solicitudes encontradas
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Actualiza una solicitud de amistad según un filtro dado y datos de actualización.
     * @param {Object} filter - Filtro para buscar la solicitud de amistad.
     * @param {Object} update - Datos de actualización para la solicitud.
     * @returns {Promise<number>} - Devuelve el número de solicitudes modificadas.
     */
    updateFriendRequest: async function (filter, update) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const friendRequestCollection = database.collection(this.collectionName); // Obtener la colección de solicitudes de amistad
            const result = await friendRequestCollection.updateOne(filter, { $set: update }); // Actualizar la solicitud
            return result.modifiedCount; // Devuelve el número de solicitudes modificadas
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Elimina solicitudes de amistad por una lista de IDs de usuarios.
     * @param {Array<ObjectId>} userIds - Lista de IDs para eliminar sus solicitudes.
     * @returns {Promise<number>} - Devuelve el número de solicitudes eliminadas.
     */
    deleteFriendRequestByUser: async function (userIds) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const friendRequestCollection = database.collection(this.collectionName); // Obtener la colección de solicitudes de amistad

            // Crear un filtro para eliminar solicitudes por IDs de remitentes o receptores
            const filter = {
                $or: [
                    { sender_id: { $in: userIds.map(id => new ObjectId(id)) } }, // Solicitudes enviadas por estos usuarios
                    { receiver_id: { $in: userIds.map(id => new ObjectId(id)) } }, // Solicitudes recibidas por estos usuarios
                ],
            };

            const result = await friendRequestCollection.deleteMany(filter); // Eliminar solicitudes que cumplen el filtro

            return result.modifiedCount; // Devuelve el número de solicitudes eliminadas
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },
};
