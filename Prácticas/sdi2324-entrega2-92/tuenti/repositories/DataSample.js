// Importar las dependencias necesarias
const { MongoClient, ObjectId } = require('mongodb'); // Cliente MongoDB y ObjectId para identificadores
const crypto = require('crypto'); // Biblioteca para encriptación y hash
const friendRequestRepository = require('./friendRequestRepository'); // Repositorio para solicitudes de amistad
const postRepository = require('./postRepository'); // Repositorio para publicaciones
const ChatRepository = require('./ChatRepository'); // Repositorio para chats

// Repositorio para inicializar y gestionar datos de muestra para la base de datos "tuenti"
class DataSampleRepository {
    // Constructor para inicializar el repositorio con la aplicación y el cliente de la base de datos
    constructor(app, dbClient) {
        this.url = 'mongodb://localhost:27017'; // URL de conexión a MongoDB
        this.dbName = 'tuenti'; // Nombre de la base de datos
        this.app = app; // Referencia a la aplicación
        this.dbClient = dbClient; // Cliente de la base de datos
    }

    // Método para eliminar todos los datos de la base de datos "tuenti"
    async deleteAll() {
        const client = new MongoClient(this.url); // Crear cliente para la conexión
        try {
            await client.connect(); // Conectar con la base de datos
            const db = client.db(this.dbName); // Obtener la referencia a la base de datos
            const collections = await db.listCollections().toArray(); // Obtener todas las colecciones
            for (let collection of collections) {
                await db.collection(collection.name).deleteMany({}); // Eliminar todos los documentos en cada colección
            }
        } catch (err) {
            console.error("Error al conectar a MongoDB:", err); // Manejo de errores
        } finally {
            await client.close(); // Cerrar la conexión
        }
    }

    // Método para inicializar los datos de muestra para la base de datos "tuenti"
    async initializeData() {
        const client = new MongoClient(this.url, { useUnifiedTopology: true }); // Crear cliente con opción de topología unificada
        try {
            await client.connect(); // Conectar con la base de datos
            console.log("Conectado correctamente");
            const db = client.db(this.dbName); // Obtener la referencia a la base de datos
            const usersCollection = db.collection('users'); // Obtener la colección de usuarios

            // Datos de prueba para usuarios
            const users = [
                { email: "uo303984@uniovi.es", name: "Eric", surname: "Vivancos", password: "Us3r@1-PASSW", role: "user" },
                { email: "user01@email.com", name: "Pedro", surname: "Alvarez", password: "Us3r@1-PASSW", role: "user" },
                { email: "user02@email.com", name: "Juan", surname: "Diaz", password: "Us3r@2-PASSW", role: "user" },
                { email: "user03@email.com", name: "Ana", surname: "Gomez", password: "Us3r@3-PASSW", role: "user" },
                { email: "user04@email.com", name: "Luis", surname: "Hernandez", password: "Us3r@4-PASSW", role: "user" },
                { email: "user05@email.com", name: "Maria", surname: "Rodriguez", password: "Us3r@5-PASSW", role: "user" },
                { email: "user06@email.com", name: "Carlos", surname: "Martinez", password: "Us3r@6-PASSW", role: "user" },
                { email: "user07@email.com", name: "Laura", surname: "Lopez", password: "Us3r@7-PASSW", role: "user" },
                { email: "user08@email.com", name: "Sergio", surname: "Sanchez", password: "Us3r@8-PASSW", role: "user" },
                { email: "user09@email.com", name: "Rosa", surname: "Fernandez", password: "Us3r@9-PASSW", role: "user" },
                { email: "user10@email.com", name: "Pablo", surname: "Ramirez", password: "Us3r@10-PASSW", role: "user" },
                { email: "user11@email.com", name: "Elena", surname: "Garcia", password: "Us3r@11-PASSW", role: "user" },
                { email: "user12@email.com", name: "Miguel", surname: "Torres", password: "Us3r@12-PASSW", role: "user" },
                { email: "user13@email.com", name: "Isabel", surname: "Morales", password: "Us3r@13-PASSW", role: "user" },
                { email: "user14@email.com", name: "Javier", surname: "Serrano", password: "Us3r@14-PASSW", role: "user" },
                { email: "user15@email.com", name: "Patricia", surname: "Ortega", password: "Us3r@15-PASSW", role: "user" },
                { email: "admin@email.com", name: "Administrador", surname: "Prueba", password: "@Dm1n1str@D0r", role: "admin" },
                { email: "user16@email.com", name: "SubirFotos", surname: "Prueba42", password: "Us3r@16-PASSW", role: "user" },
                { _id: new ObjectId('123456789012345678901234'), email: "prueba42@email.com", name: "Prueba42", surname: "Prueba42", password: "hola", role: "user" },
            ];

            // Encriptar las contraseñas usando HMAC con SHA-256
            const clave = 'abcdefg'; // Clave secreta para la encriptación
            users.forEach(user => {
                let securePassword = crypto.createHmac('sha256', clave) // Crear hash con SHA-256
                    .update(user.password) // Actualizar con la contraseña
                    .digest('hex'); // Obtener el resultado como hexadecimal
                user.password = securePassword; // Reemplazar la contraseña original con la encriptada
            });

            // Insertar los datos de usuarios en la colección "users"
            let result = await usersCollection.insertMany(users);

            // Insertar publicaciones para cada usuario
            for (const user of users) {
                const foundUser = await usersCollection.findOne({ email: user.email }); // Encontrar el usuario en la colección
                const userId = foundUser._id; // Obtener el ID del usuario

                // Insertar 10 publicaciones para cada usuario
                for (let i = 0; i < 10; i++) {
                    const post = {
                        user_id: userId, // Vincular con el usuario correspondiente
                        title: `Post ${i + 1} de ${user.name}`, // Título de la publicación
                        content: `Este es el contenido del post ${i + 1} de ${user.name}.`, // Contenido de la publicación
                        created_at: new Date(), // Fecha y hora de creación
                    };
                    await postRepository.insertPost(post); // Insertar la publicación
                }
            }

            // Insertar solicitudes de amistad
            for (let i = 0; i < 15; i++) {
                if (i !== 9) { // Evitar la autointeracción
                    let friend_request = {
                        date: new Date(), // Fecha y hora de la solicitud
                        status: "pending", // Estado de la solicitud
                        receiver_id: result.insertedIds[9], // ID del receptor de la solicitud
                        sender_id: result.insertedIds[i], // ID del remitente de la solicitud
                    };
                    await friendRequestRepository.insertFriendRequest(friend_request); // Insertar la solicitud de amistad
                }
            }

            // Crear amistades para usuarios
            const user1Id = result.insertedIds[0]; // ID del primer usuario
            const user2Id = result.insertedIds[1]; // ID del segundo usuario
            const user3Id = result.insertedIds[2]; // ID del tercer usuario

            const friendshipCollection = db.collection('friendship'); // Colección de amistades

            // Amistades entre usuarios
            const friendships = [
                { date: new Date(), friend_id: user2Id, user_id: user1Id }, // Amistad entre el primer y el segundo usuario
                { date: new Date(), friend_id: user1Id, user_id: user2Id }, // Amistad revertida entre el segundo y el primer usuario
                { date: new Date(), friend_id: user3Id, user_id: user1Id }, // Amistad entre el primer y el tercer usuario
                { date: new Date(), friend_id: user1Id, user_id: user3Id }, // Amistad revertida entre el tercer y el primer usuario
                { date: new Date(), friend_id: new ObjectId('123456789012345678901234'), user_id: user1Id }, // Amistad entre el primer usuario y "Prueba42"
                { date: new Date(), friend_id: user1Id, user_id: new ObjectId('123456789012345678901234') }, // Amistad revertida entre "Prueba42" y el primer usuario
            ];

            // Insertar las amistades en la base de datos
            await friendshipCollection.insertMany(friendships);

            // Insertar chats con mensajes
            const collectionChats = db.collection('chats'); // Colección de chats

            // Primer chat entre el primer y el segundo usuario
            const chat1 = {
                _id: new ObjectId('223456789012345678901234'),
                userId: new ObjectId(user1Id.toString()), // ID del primer usuario
                friendId: new ObjectId(user2Id.toString()), // ID del segundo usuario
                messages: [
                    { senderId: new ObjectId(user1Id.toString()), content: "¡Hola, amigo!", timestamp: new Date(), read: false, _id: new ObjectId('323456789012345678901234') },
                    { senderId: new ObjectId(user2Id.toString()), content: "¡Hola, qué tal!", timestamp: new Date(), read: false, _id: new ObjectId() },
                ],
                roomName: user1Id < user2Id ? `${user1Id}-${user2Id}` : `${user2Id}-${user1Id}`, // Nombre de la sala
            };
            await collectionChats.insertOne(chat1); // Insertar el primer chat

            // Segundo chat entre el primer y el tercer usuario
            const chat2 = {
                userId: new ObjectId(user1Id.toString()), // ID del primer usuario
                friendId: new ObjectId(user3Id.toString()), // ID del tercer usuario
                messages: [
                    { senderId: new ObjectId(user1Id.toString()), content: "¡Hola, mundo!", timestamp: new Date(), read: false, _id: new ObjectId() },
                    { senderId: new ObjectId(user3Id.toString()), content: "¡Hola! :)", timestamp: new Date(), read: false, _id: new ObjectId() },
                ],
                roomName: user1Id < user3Id ? `${user1Id}-${user3Id}` : `${user3Id}-${user1Id}`, // Nombre de la sala
            };
            await collectionChats.insertOne(chat2); // Insertar el segundo chat

            console.log("Datos de muestra insertados correctamente");
        } catch (err) {
            console.error("Error al conectar a MongoDB:", err); // Manejo de errores
        } finally {
            await client.close(); // Cerrar la conexión
        }
    }
}

// Exportar el repositorio para ser utilizado en otros lugares
module.exports = DataSampleRepository;
