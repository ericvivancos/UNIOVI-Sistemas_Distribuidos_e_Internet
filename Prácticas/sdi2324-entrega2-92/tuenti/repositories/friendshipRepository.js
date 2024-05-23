// Importar las dependencias necesarias
const { ObjectId } = require("mongodb"); // ObjectId para trabajar con identificadores únicos de MongoDB

/**
 * Repositorio para gestionar amistades en una base de datos MongoDB.
 */
module.exports = {
    mongoClient: null, // Cliente de la base de datos
    app: null, // Aplicación asociada al repositorio
    database: "tuenti", // Nombre de la base de datos
    collectionName: "friendship", // Nombre de la colección para amistades

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
     * Inserta una nueva amistad en la base de datos.
     * @param {Object} friendship - Objeto que representa la amistad.
     * @returns {Promise<ObjectId>} - Devuelve el ID de la amistad insertada.
     */
    insertFriendship: async function (friendship) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const friendshipCollection = database.collection(this.collectionName); // Obtener la colección de amistades
            const result = await friendshipCollection.insertOne(friendship); // Insertar la amistad
            return result.insertedId; // Devuelve el ID de la amistad insertada
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Encuentra todas las amistades que cumplan con un filtro dado.
     * @param {Object} filter - Filtro para buscar las amistades.
     * @param {Object} [options] - Opciones adicionales para la búsqueda.
     * @returns {Promise<Array>} - Devuelve un array con todas las amistades encontradas.
     */
    findAllFriendships: async function (filter, options) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const friendshipCollection = database.collection(this.collectionName); // Obtener la colección de amistades
            const friendships = await friendshipCollection.find(filter, options).toArray(); // Buscar todas las amistades que cumplan el filtro
            return friendships; // Devuelve el array de amistades encontradas
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Cuenta el número de amistades que cumplen con un filtro dado.
     * @param {Object} filter - Filtro para contar amistades.
     * @returns {Promise<number>} - Devuelve el número de amistades que cumplen el filtro.
     */
    count: async function (filter) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const friendshipCollection = database.collection(this.collectionName); // Obtener la colección de amistades
            const count = await friendshipCollection.countDocuments(filter); // Contar el número de amistades que cumplen el filtro
            return count; // Devuelve el número de amistades encontradas
        } catch (error) {
            throw new Error("Error al contar amistades: " + error.message); // Lanzar error si algo falla
        }
    },

    /**
     * Elimina amistades por una lista de IDs de usuarios.
     * @param {Array<ObjectId>} userIds - Lista de IDs de usuarios para eliminar sus amistades.
     * @returns {Promise<number>} - Devuelve el número de amistades eliminadas.
     */
    deleteFriendshipByUser: async function (userIds) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const friendshipCollection = database.collection(this.collectionName); // Obtener la colección de amistades
            const filter = {
                $or: [
                    { friend_id: { $in: userIds.map(id => new ObjectId(id)) } }, // Amistades donde los usuarios son amigos
                    { user_id: { $in: userIds.map(id => new ObjectId(id)) } }, // Amistades donde los usuarios son los principales
                ],
            }; // Filtro para eliminar amistades por usuario
            const result = await friendshipCollection.deleteMany(filter); // Eliminar las amistades
            return result.modifiedCount; // Devuelve el número de amistades eliminadas
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },
};
