const express = require("express");
const path = require("path");
const cookieParser = require("cookie-parser");
const logger = require("morgan");
const session = require('express-session');
const crypto = require("crypto");
const bodyParser = require("body-parser");
const {MongoClient} = require("mongodb");
const createError = require("http-errors");
const paginate = require("express-paginate");
const jwt = require("jsonwebtoken");
const app = express();
const flash = require("connect-flash");


// Configuración de sesiones
app.use(session({
  secret: 'a3s5d46a5sd46as5d4as6d5as4d',//firma la cokie puedes obviarlo si quieres
  resave: false,
  saveUninitialized: true,
  cookie: { maxAge: 60000 }
}));

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Credentials", "true");
  res.header("Access-Control-Allow-Methods", "POST, GET, DELETE, UPDATE, PUT");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
// Debemos especificar todas las headers que se aceptan. Content-Type , token
  next();
});

app.use(flash());

// Configuraciones de Express
app.use(logger("dev"));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, "public")));

// Configuración de vistas
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'twig');

// Variables globales
app.set('clave','abcdefg');
app.set('crypto',crypto);
app.set("jwt",jwt);

// Configuración de MongoDB
const connectionStrings='mongodb://localhost:27017/';
const dbClient= new MongoClient(connectionStrings);
app.set('db', dbClient);

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Configuración de la paginación
app.use(paginate.middleware(10, 50));
app.set('paginate', paginate); // Agrega paginate al objeto app
app.set('maxSizePerPage', 5); // Agrega paginate al objeto app

// Repositorios
const usersRepository = require("./repositories/usersRepository.js");
const friendshipRepository = require("./repositories/friendshipRepository.js");
const friendRequestRepository = require("./repositories/friendRequestRepository.js");
const postRepository = require("./repositories/postRepository.js");
const ChatRepository = require("./repositories/ChatRepository.js");
const logRepository = require("./repositories/logsRepository");


usersRepository.init(app, dbClient);
friendshipRepository.init(app, dbClient);
friendRequestRepository.init(app, dbClient);
postRepository.init(app,dbClient);
const chatRepository = new ChatRepository(app, dbClient);
chatRepository.init();
const DataSample = require("./repositories/DataSample.js");
let dataSample = new DataSample();
logRepository.init(app,dbClient);

// Middlewares
const flashMessages = require("./middlewares/flash");
flashMessages(app);
const Logger = require("./middlewares/logger");
app.use(Logger(logRepository));
// Rutas
const indexRouter = require("./routes/index");
const userTokenRouter = require("./routes/userTokenRouter");


app.use('/', indexRouter);

//le pasas el app ara que tenga crypto y demas

//Rutas que requieren autenticación o roles específicos

// Rutas específicas
require("./routes/DataSample.js")(app, dataSample);
require("./routes/users.js")(app, usersRepository, friendshipRepository, friendRequestRepository);
require("./routes/friendRequest.js")(app, usersRepository, friendRequestRepository);
require("./routes/posts.js")(app,usersRepository,postRepository);
require("./routes/friendship.js")(app, friendshipRepository, usersRepository,postRepository);
require("./routes/logs")(app,logRepository);

// Rutas de API
app.use("/api/v1.0/chats", userTokenRouter);
require("./routes/api/APITuentiv1.0.js")(app, usersRepository, friendshipRepository, chatRepository);

// Manejo de errores 404
app.use(function(req, res, next) {
  next(createError(404));
});

// Middleware de error
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});



// Configuración para `socket.io`
app.use("/socket.io", express.static(path.join(__dirname, "node_modules", "socket.io", "client-dist")));
app.use(express.static(path.join(__dirname, "views", "chats")));

module.exports = app;

