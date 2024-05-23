// Importar las dependencias necesarias
const express = require('express'); // Framework para la creación de servidores web con Node.js

// Función para crear un middleware de logging
function Logger(logsRepository) {
    // Crear un router de Express para aplicar el middleware
    const router = express.Router();

    // Middleware para registrar logs
    router.use((req, res, next) => {
        // Definir el tipo de log según la URL
        let type = "PET"; // "PET" es un tipo genérico para peticiones
        if (req.url.includes("signup")) { // Si la URL contiene "signup", el tipo es "ALTA"
            type = "ALTA"; // Registro de altas (por ejemplo, registros nuevos)
        } else if (req.url.includes("login")) { // Si la URL contiene "login", el tipo es "LOGIN"
            type = "LOGIN"; // Registro de inicio de sesión
        } else if (req.url.includes("logout")) { // Si la URL contiene "logout", el tipo es "LOGOUT"
            type = "LOGOUT"; // Registro de cierre de sesión
        }

        // Crear el objeto de log
        const log = {
            date: new Date(), // Fecha y hora del log
            action: req.method, // Método HTTP de la petición (GET, POST, etc.)
            url: req.originalUrl, // URL original de la petición
            type: type, // Tipo de log según lo definido anteriormente
            user: req.session?.user?.email || "anónimo", // Correo del usuario si está autenticado, o "anónimo"
            ip: req.ip, // Dirección IP del cliente
        };

        // Insertar el log en el repositorio de logs
        logsRepository.insertLog(log).catch((error) => { // Manejo de errores si falla la inserción
            console.error("No se pudo registrar el log:", error); // Imprimir el error en consola
        });

        next(); // Continuar al siguiente middleware
    });

    return router; // Devolver el router con el middleware de logging
}

// Exportar el middleware de logging para ser utilizado en otros lugares
module.exports = Logger;
