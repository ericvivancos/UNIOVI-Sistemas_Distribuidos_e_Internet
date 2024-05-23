/*
verifica el token desencriptandolo y si han pasasdo mas de 240 segundos desde que se creo indicas que caduco
si lo desencriptas bien dejas pasar a la sigueitne peticion next
 */

const jwt = require("jsonwebtoken");
const express = require('express');
const userTokenRouter = express.Router();
userTokenRouter.use(function (req, res, next) {
    console.log("userAuthorRouter");
    console.log(req.body);
    let token = req.headers['token'] || req.body.token || req.query.token;
    if (token != null) {
        // verificar el token
        jwt.verify(token, 'secreto', {}, function (err, infoToken) {
            if (err || (Date.now() / 1000 - infoToken.time) > 900) {
                res.status(403); // Forbidden
                res.json({
                    authorized: false,
                    error: 'Token inválido o caducado'
                });
            } else {
                // dejamos correr la petición
                res.user = infoToken.user;
                res.userId = infoToken.id;
                console.log('userTokenRouter el res.user es ', res.user,'id es ',res.userId);
                next();
            }
        });
    } else {
        res.status(403); // Forbidde
        res.json({
            authorized: false,
            error: 'No hay Token'
        });
    }
});
module.exports = userTokenRouter;