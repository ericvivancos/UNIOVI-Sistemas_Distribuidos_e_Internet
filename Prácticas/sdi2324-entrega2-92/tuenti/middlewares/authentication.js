// Importar ObjectId de MongoDB para trabajar con identificadores únicos
const { ObjectId } = require("mongodb");

// Middleware para requerir autenticación
function requireAuth(req, res, next) {
    // Si no hay un usuario en la sesión, redirigir a la página de inicio de sesión con un mensaje de error
    if (!req.session.user) {
        req.flash("error", ["Debes estar autenticado para acceder a esta página."]);
        return res.redirect("/users/login"); // Redirigir a la página de inicio de sesión
    }
    next(); // Continuar con el siguiente middleware si el usuario está autenticado
}

// Middleware para requerir un rol específico
function requireRole(role) {
    return (req, res, next) => {
        // Comprobar si el usuario está autenticado y tiene el rol requerido
        if (!req.session.user || req.session.user.role !== role) {
            req.flash("error", ["No tienes permisos para acceder a esta página."]);
            return res.redirect("/users/login"); // Redirigir a la página de inicio de sesión
        }
        next(); // Continuar con el siguiente middleware si el usuario tiene el rol requerido
    };
}

// Middleware para requerir que dos usuarios sean amigos (para endpoints no REST)
function requireFriendship(friendshipRepository) {
    return async (req, res, next) => {
        try {
            // Convertir los IDs a ObjectId para comparaciones
            const userId = new ObjectId(req.session.user._id); // ID del usuario actual
            const friendId = new ObjectId(req.params.id); // ID del amigo, pasado por parámetro

            // Crear un filtro para comprobar si existe amistad entre ambos usuarios
            const filter = {
                $or: [
                    { user_id: userId, friend_id: friendId }, // Caso en que el usuario actual sea el primer miembro de la amistad
                    { user_id: friendId, friend_id: userId }, // Caso en que el amigo sea el primer miembro de la amistad
                ],
            };

            // Buscar amistades con el filtro definido
            const friendship = await friendshipRepository.findAllFriendships(filter, {});
            if (!friendship || friendship.length === 0) { // Si no existe la amistad
                req.flash("error", ["No tienes permiso para ver esta página."]); // Mensaje de error
                return res.redirect("/listUsersSocialMedia"); // Redirigir a otra página
            }
            next(); // Continuar con el siguiente middleware si existe la amistad
        } catch (error) {
            console.error("Error verificando amistad:", error); // Manejo de errores
            req.flash("error", ["Error interno al verificar la amistad."]); // Mensaje de error
            return res.redirect("/listUsersSocialMedia"); // Redirigir en caso de error
        }
    };
}

// Middleware para requerir amistad (para endpoints REST)
function requireRestFriendship(friendshipRepository) {
    return async (req, res, next) => {
        try {
            // Convertir IDs a ObjectId
            const userId = new ObjectId(res.userId); // ID del usuario autenticado
            const friendId = new ObjectId(req.params.otherId); // ID del amigo desde los parámetros de la solicitud

            // Prevenir que el usuario interactúe consigo mismo de forma no permitida
            if (userId.equals(friendId)) {
                res.status(400).json({ error: "No puedes interactuar contigo mismo de esta manera." }); // Responder con error si intenta interactuar consigo mismo
                return;
            }

            // Crear un filtro para comprobar la existencia de amistad
            const filter = {
                $or: [
                    { user_id: userId, friend_id: friendId }, // Si el usuario actual es el primer miembro de la amistad
                    { user_id: friendId, friend_id: userId }, // Si el amigo es el primer miembro de la amistad
                ],
            };

            // Buscar la amistad con el filtro definido
            const friendship = await friendshipRepository.findAllFriendships(filter, {});
            if (!friendship || friendship.length === 0) { // Si no existe la amistad
                res.status(403).json({ error: "No tienes permiso para realizar esta acción." }); // Responder con error 403
                return;
            }

            next(); // Continuar si existe la amistad
        } catch (error) {
            console.error("Error verificando amistad:", error); // Manejo de errores
            res.status(500).json({ error: "Error interno al verificar la amistad." }); // Responder con error 500 en caso de problemas internos
        }
    };
}

// Exportar los middleware para su uso en otras partes del código
module.exports = {
    requireAuth, // Middleware para requerir autenticación
    requireRole, // Middleware para requerir un rol específico
    requireFriendship, // Middleware para requerir amistad en endpoints no REST
    requireRestFriendship, // Middleware para requerir amistad en endpoints REST
};
