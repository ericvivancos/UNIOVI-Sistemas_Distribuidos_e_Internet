/**
 * Repositorio para gestionar publicaciones en una base de datos MongoDB.
 */
module.exports = {
    mongoClient: null, // Cliente de la base de datos
    app: null, // Aplicación asociada al repositorio
    database: "tuenti", // Nombre de la base de datos
    collectionName: "posts", // Nombre de la colección para publicaciones

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
     * Inserta una nueva publicación en la base de datos.
     * @param {Object} post - Objeto que representa la publicación.
     * @returns {Promise<ObjectId>} - Devuelve el ID de la publicación insertada.
     */
    insertPost: async function (post) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const postsCollection = database.collection(this.collectionName); // Obtener la colección de publicaciones
            const result = await postsCollection.insertOne(post); // Insertar la publicación
            return result.insertedId; // Devuelve el ID de la publicación insertada
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Encuentra la última publicación que cumpla con un filtro dado.
     * @param {Object} filter - Filtro para buscar la publicación.
     * @param {Object} [options] - Opciones adicionales para la búsqueda.
     * @returns {Promise<Object|null>} - Devuelve la última publicación encontrada o null si no se encuentra ninguna.
     */
    findLastPost: async function (filter, options) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const postsCollection = database.collection(this.collectionName); // Obtener la colección de publicaciones
            const result = await postsCollection
                .find(filter, options) // Buscar con el filtro y opciones
                .sort({ created_at: -1 }) // Ordenar por fecha de creación en orden descendente
                .limit(1) // Limitar a un resultado
                .toArray(); // Convertir el resultado a un array
            return result.length > 0 ? result[0] : null; // Devolver el primer elemento del array o null si está vacío
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Encuentra publicaciones paginadas con un filtro y limitación de página.
     * @param {Object} filter - Filtro para buscar publicaciones.
     * @param {number} page - Número de la página actual.
     * @param {number} limit - Número máximo de publicaciones por página.
     * @returns {Promise<Array>} - Devuelve un array con las publicaciones encontradas.
     */
    findPostsPaginated: async function (filter, page, limit) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const postsCollection = database.collection(this.collectionName); // Obtener la colección de publicaciones
            const skip = (page - 1) * limit; // Calcular el número de elementos a saltar
            const result = await postsCollection
                .find(filter) // Buscar con el filtro
                .sort({ created_at: -1 }) // Ordenar por fecha de creación en orden descendente
                .limit(limit) // Limitar el número de publicaciones por página
                .skip(skip) // Saltar los elementos según la página actual
                .toArray(); // Convertir el resultado a un array
            return result; // Devuelve el array de publicaciones encontradas
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },

    /**
     * Cuenta el número de publicaciones que cumplen con un filtro dado.
     * @param {Object} filter - Filtro para contar publicaciones.
     * @returns {Promise<number>} - Devuelve el número total de publicaciones que cumplen el filtro.
     */
    count: async function (filter) {
        try {
            await this.dbClient.connect(); // Conectar a la base de datos
            const database = this.dbClient.db(this.database); // Obtener la referencia a la base de datos
            const postsCollection = database.collection(this.collectionName); // Obtener la colección de publicaciones
            const count = await postsCollection.countDocuments(filter); // Contar el número de publicaciones
            return count; // Devuelve el número total de publicaciones
        } catch (error) {
            throw error; // Lanza el error para el manejo de excepciones
        }
    },
};
