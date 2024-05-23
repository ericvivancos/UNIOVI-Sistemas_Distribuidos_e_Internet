/**
 * Repositorio para gestionar registros de logs en una base de datos MongoDB.
 */
module.exports = {
    mongoClient: null, // Cliente de la base de datos
    app: null, // Aplicación asociada al repositorio
    database: "tuenti", // Nombre de la base de datos
    collectionName: "logs", // Nombre de la colección para logs

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
     * Busca logs en la base de datos usando un filtro dado.
     * @param {Object} filter - Filtro para buscar logs.
     * @param {Object} [options] - Opciones adicionales para la búsqueda.
     * @returns {Promise<Array>} - Devuelve un array con los logs que cumplen el filtro.
     */
    findLogs: async function (filter, options) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const logsCollection = database.collection(this.collectionName); // Obtener la colección de logs
            const logs = await logsCollection.find(filter, options).toArray(); // Buscar los logs que cumplen el filtro
            return logs; // Devuelve el array de logs encontrados
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Inserta un nuevo registro de log en la base de datos.
     * @param {Object} log - Objeto que representa el log.
     * @returns {Promise<ObjectId>} - Devuelve el ID del log insertado.
     */
    insertLog: async function (log) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const logsCollection = database.collection(this.collectionName); // Obtener la colección de logs
            const result = await logsCollection.insertOne(log); // Insertar el log
            return result.insertedId; // Devuelve el ID del log insertado
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Elimina logs de la base de datos usando un filtro dado.
     * @param {Object} [filter={}] - Filtro para eliminar logs. Por defecto, elimina todos los logs.
     * @returns {Promise<Object>} - Devuelve el resultado de la operación de eliminación.
     */
    deleteLogs: async function (filter = {}) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const logsCollection = database.collection(this.collectionName); // Obtener la colección de logs
            const result = await logsCollection.deleteMany(filter); // Eliminar los logs que cumplen el filtro
            return result; // Devuelve el resultado de la operación de eliminación
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },
};
