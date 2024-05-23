// Importar dependencias necesarias
const { ObjectId } = require("mongodb"); // ObjectId para trabajar con identificadores únicos de MongoDB

/**
 * Repositorio para gestionar usuarios en una base de datos MongoDB.
 */
module.exports = {
    mongoClient: null, // Cliente de la base de datos
    app: null, // Aplicación asociada al repositorio
    database: "tuenti", // Nombre de la base de datos
    collectionName: "users", // Nombre de la colección para usuarios

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
     * Inserta un nuevo usuario en la base de datos.
     * @param {Object} user - Objeto que representa el usuario.
     * @returns {Promise<ObjectId>} - Devuelve el ID del usuario insertado.
     */
    insertUser: async function (user) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const usersCollection = database.collection(this.collectionName); // Obtener la colección de usuarios
            const result = await usersCollection.insertOne(user); // Insertar el usuario
            return result.insertedId; // Devuelve el ID del usuario insertado
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Busca un usuario que cumpla con un filtro dado.
     * @param {Object} filter - Filtro para buscar el usuario.
     * @param {Object} [options] - Opciones adicionales para la búsqueda.
     * @returns {Promise<Object|null>} - Devuelve el usuario encontrado o null si no se encuentra ninguno.
     */
    findUser: async function (filter, options) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const usersCollection = database.collection(this.collectionName); // Obtener la colección de usuarios
            const user = await usersCollection.findOne(filter, options); // Buscar el usuario
            return user; // Devuelve el usuario encontrado
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Encuentra usuarios que cumplan con un filtro dado.
     * @param {Object} filter - Filtro para buscar usuarios.
     * @param {Object} [options] - Opciones adicionales para la búsqueda.
     * @returns {Promise<Array>} - Devuelve un array con los usuarios que cumplen el filtro.
     */
    findUsersByFilter: async function (filter, options) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const usersCollection = database.collection(this.collectionName); // Obtener la colección de usuarios
            const users = await usersCollection.find(filter, options).toArray(); // Buscar los usuarios que cumplen el filtro
            return users; // Devuelve el array de usuarios encontrados
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Encuentra todos los usuarios paginados.
     * @param {number} page - Número de la página actual.
     * @param {number} pageSize - Número máximo de usuarios por página.
     * @returns {Promise<Array>} - Devuelve un array con los usuarios en la página.
     */
    findAllUsers: async function (page, pageSize) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const usersCollection = database.collection(this.collectionName); // Obtener la colección de usuarios

            // Calcular cuántos documentos saltar para la paginación
            const skip = (page - 1) * pageSize;

            // Usar skip y limit para obtener la página actual
            const users = await usersCollection.find().skip(skip).limit(pageSize).toArray(); // Buscar usuarios con paginación

            return users; // Devuelve el array de usuarios encontrados
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Encuentra usuarios paginados que cumplan con un filtro dado.
     * @param {Object} filter - Filtro para buscar usuarios.
     * @param {number} page - Número de la página actual.
     * @param {number} pageSize - Número máximo de usuarios por página.
     * @returns {Promise<Array>} - Devuelve un array con los usuarios que cumplen el filtro en la página actual.
     */
    findFilteredPaginatedUsers: async function (filter, page, pageSize) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const usersCollection = database.collection(this.collectionName); // Obtener la colección de usuarios

            // Calcular cuántos documentos saltar para la paginación
            const skip = (page - 1) * pageSize;

            // Usar skip y limit para obtener la página actual con filtro
            const users = await usersCollection.find(filter).skip(skip).limit(pageSize).toArray(); // Buscar usuarios con paginación y filtro

            return users; // Devuelve el array de usuarios encontrados
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Cuenta todos los usuarios en la colección.
     * @returns {Promise<number>} - Devuelve el número total de usuarios.
     */
    countAllUsers: async function () {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const usersCollection = database.collection(this.collectionName); // Obtener la colección de usuarios
            const count = await usersCollection.countDocuments(); // Contar todos los documentos en la colección
            return count; // Devuelve el número total de usuarios
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Cuenta usuarios que cumplan con un filtro dado.
     * @param {Object} filter - Filtro para contar usuarios.
     * @returns {Promise<number>} - Devuelve el número total de usuarios que cumplen el filtro.
     */
    countFilteredUsers: async function (filter) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const usersCollection = database.collection(this.collectionName); // Obtener la colección de usuarios
            const count = await usersCollection.countDocuments(filter); // Contar usuarios que cumplen el filtro
            return count; // Devuelve el número total de usuarios que cumplen el filtro
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Actualiza un usuario según un filtro dado.
     * @param {Object} filter - Filtro para buscar el usuario.
     * @param {Object} update - Datos de actualización para el usuario.
     * @returns {Promise<number>} - Devuelve el número de usuarios actualizados.
     */
    updateUser: async function (filter, update) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const usersCollection = database.collection(this.collectionName); // Obtener la colección de usuarios

            const result = await usersCollection.updateOne(filter, { $set: update }); // Actualizar el usuario según el filtro

            return result.modifiedCount; // Devuelve el número de usuarios modificados
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Elimina usuarios por una lista de IDs.
     * @param {Array<ObjectId>} userIds - Lista de IDs de usuarios a eliminar.
     * @returns {Promise<number>} - Devuelve el número de usuarios eliminados.
     */
    deleteUser: async function (userIds) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const usersCollection = database.collection(this.collectionName); // Obtener la colección de usuarios

            const filter = {
                _id: {
                    $in: userIds.map(id => new ObjectId(id)) // Crear filtro con la lista de IDs
                },
            }; // Filtro para eliminar usuarios

            const result = await usersCollection.deleteMany(filter); // Eliminar usuarios

            return result.modifiedCount; // Devuelve el número de usuarios eliminados
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },
};
