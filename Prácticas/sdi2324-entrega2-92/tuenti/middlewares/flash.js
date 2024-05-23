// flashMiddleware.js

module.exports = (app) => {
    // Middleware para inicializar la funcionalidad de flash messages
    app.use((req, res, next) => {
        if (req.session) {
            // Si la sesión existe, agregar la función `flash` a `req`
            req.flash = (type, msg) => {
                if (!req.session.flash) {
                    req.session.flash = {}; // Crear un objeto flash si no existe
                }
                req.session.flash[type] = msg; // Añadir el mensaje flash bajo el tipo especificado
            };
        } else {
            req.flash = () => {}; // Si no hay sesión, dejar `flash` como una función vacía para evitar errores
        }
        next(); // Continuar al siguiente middleware
    });

    // Middleware para mostrar mensajes flash
    app.use((req, res, next) => {
        // Hacer que los mensajes flash estén disponibles como variables locales en las vistas
        res.locals.flash = req.session?.flash || {}; // Recuperar mensajes flash de la sesión
        if (req.session) {
            req.session.flash = {}; // Limpiar los mensajes flash después de usarlos para evitar reutilización
        }
        next(); // Continuar al siguiente middleware
    });

    // Middleware para compartir la sesión con las plantillas
    app.use((req, res, next) => {
        res.locals.session = req.session; // Hacer la sesión disponible para las plantillas
        next(); // Continuar al siguiente middleware
    });
};
