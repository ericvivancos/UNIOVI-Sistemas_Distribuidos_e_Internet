const {ObjectId} = require("mongodb");
const roles = [ "user", "admin"];

const {requireAuth, requireRole} = require("../middlewares/authentication");
module.exports = function (app, usersRepository, friendshipRepository, friendRequestRepository) {

  /**
   * Edita los datos de un usuario
   */
  app.post("/users/edit/:_id",requireRole("admin"), async function (req, res) {
    let errors = []

    // Validar los datos del formulario
    let validationError = await validateDataEdit(req);
    if (validationError) {
      return res.redirect('/users/edit/' + req.params._id);
    }

    let filter = { _id: new ObjectId(req.params._id) };

    // Actualizar el usuario en la base de datos
    usersRepository.updateUser(filter, {
      name: req.body.name,
      surname: req.body.surname,
      role: req.body.role,
      email: req.body.email
    }).then(() => {
      // Redirigir al usuario a la lista de usuarios con un mensaje de éxito
      let suc = []
      suc.push('Usuario actualizado con éxito');
      req.flash('succes', suc)
      res.redirect('/listAllUsers');
    }).catch(error => {
      // Manejar el error y redirigir al usuario a la página de edición con un mensaje de error
      errors.push('Error al actualizar el usuario');
      req.flash('error', errors);
      res.redirect('/users/edit/' + req.params.email);
    });


  });

  /**
   * Muestra la vista de edición de un usuario
   */
  app.get("/users/edit/:_id",requireRole("admin"), function (req, res) {
    let errors = []



    let filter = { _id: new ObjectId(req.params._id) };
    usersRepository.findUser(filter, {}).then(user => {
      let  userParalaVista={
        email: user.email,
        name:user.name,
        surname:user.surname,
        role: user.role,
        _id: user._id
      };

      //no le pasas el user completo
      res.render('user/edit.twig', { user: userParalaVista });
    }).catch(error => {
      errors.push('Error al buscar el usuario');
      req.flash('error', errors);
      res.redirect('/listAllUsers');
    } );




  });

  /**
   * Muestra la vista de Identificación de usuarios
   */
  app.get('/users/login', function (req, res) {
    //console.log('errores actuales',req.flash('error'));
    //res.render('login.twig', { errors: req.flash('error') });
    //limpiar el flash
    res.render('login.twig');
  })

  /**
   * Inicio de sesión de los usuarios que dependiendo de su rol se les redirecciona a una página diferente
   */
  app.post('/users/login', async function (req,res)
      {
        let errors = []
        let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');

        let validationError =await  validateDataLogin(req,securePassword);
        if(validationError){
          return res.redirect('/users/login');
        }


        let filter = {email: req.body.email, password: securePassword};
        let options = {};
        //redireccionamos a la pagina en funcion de su rango si es admin

        usersRepository.findUser(filter, options).then(user => {
          if(user!==null && user!==undefined){
            console.log('usuario encontrado',user);
            req.session.user=user;
            if(user.role===roles[1]){
              res.redirect("/listAllUsers");
            }else if (user.role===roles[0]){
              res.redirect('/listUsersSocialMedia');

            }

          }else if (user===null || user===undefined) {
            errors.push('Se ha producido un error al buscar el usuario');
            req.flash('error', errors);
            res.redirect('/users/login');

          }else{
            req.session=null;
            errors.push('Email o password incorrecto');
            req.flash('error', errors);
            res.redirect('/users/login');

          }
        }).catch(error => {
          req.session=null;
          errors.push("Error de conexión");
          req.flash('error', errors);
          res.redirect('/users/login');
        });
      }
  )

  /**
   * Muestra la vista de Registro de usuarios
   */
  app.get('/users/signup',  function (req, res) {
    res.render('signup.twig', { errors: req.flash('error') });

  })

  /**
   * Registra los usuarios comprobando si los datos introducidos son válidos
   */
  app.post('/users/signup', async function (req, res) {

    let validationError = await validateDataSingup(req);
    if (validationError) {
      return res.redirect('/users/signup');
    }

    let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
        .update(req.body.password).digest('hex');
    let user = {
      email: req.body.email,
      password: securePassword,
      role:roles[0],
    }
    usersRepository.insertUser(user).then(userId => {
      //res.send('Usuario registrado ' + userId);
      res.redirect('/users/login' );

    }).catch(error => {
      res.render("signup.twig", { errors: req.flash('error') });
    });

  });

  /**
   * Termina con la sesión del usuario actual
   */
  app.get('/users/logout', requireAuth,function (req, res) {
    try{

      req.session.user = null;
      let suc=[];
      suc.push('Has cerrado sesión correctamente');
      req.flash('success', suc);
      //res.send("El usuario se ha desconectado correctamente");
      res.redirect('/users/login');



    }catch(error){
      console.error("Error durante el cierre de sesión:", error);
      req.flash("error", "Error al cerrar sesión.");
      res.redirect("/users/login");
    }
  });

  /**
   * Comprobaciones de si los datos del registro son válidos
   * @param req
   * @returns {Promise<boolean>}
   */
  async function validateDataSingup(req){
    console.log('entra validar Singup datos');
    console.log('datos del formulario',req.body);
    let errors = [];

    // Validación de los datos del formulario
    if (!req.body.name || req.body.name.length < 3) {
      errors.push('El nombre debe tener al menos 3 caracteres');
    }
    if (!req.body.surname || req.body.surname.length < 3) {
      errors.push('Los apellidos deben tener al menos 3 caracteres');
    }
    if (!req.body.email || !req.body.email.includes('@')) {
      errors.push('El email debe ser válido');
    }
    if(!req.body.birthdate || req.body.birthdate.toString().trim().length===0){
      errors.push('La fecha de nacimiento no puede estar vacía');
    }
    if (!req.body.password || req.body.password.length < 8 ) {
      errors.push('La contraseña debe tener al menos 8 caracteres');
    }
    if (req.body.password !== req.body.passwordConfirm || req.body.passwordConfirm.trim().length < 8) {
      errors.push('Las contraseñas no coinciden');
    }



    //comprobar si el email ya esta registrado
    return await validateIfEmailAlreadyExists(req, usersRepository, errors);
  }

  async function validateDataLogin(req,securePassword){
    let errors = [];
    // Validación de los datos del formulario tantao email como contraseña
    if (!req.body.email || !req.body.email.includes('@')|| req.body.email.trim().length===0) {
      errors.push('El email debe ser válido');
    }
    if (!req.body.password || req.body.password.length < 8 || req.body.password.trim().length===0) {
      errors.push('La contraseña debe tener al menos 8 caracteres');
    }
    //comprobar si el email ya esta registrado
    let filter = {email: req.body.email};
    let options = {};
    let usuario;
    try {
      let user = await usersRepository.findUser(filter, options);
      if((user===null || user===undefined)&&req.body.email.trim().length>0){//solo se muestra si el email no esta vacia sino no se muestra
        errors.push('El email no está registrado');
      }else
      {
        usuario=user;
      }
    } catch(e) {
      errors.push('Error al buscar el email en la base de datos');
    }

    //compobar is la contra del user conicde con la del formulario
    if(usuario!==null && usuario!==undefined && usuario.password!==securePassword){
      errors.push('La contraseña no es correcta');
    }

    //devolver los errores
    if (errors.length > 0) {
      console.log('errores encontrados en el login',errors);
      req.flash('error', errors);
    }
    return errors.length>0;
  }
  app.get('/users', function (req, res) {
    res.send('lista de usuarios');
  });

  app.get('/deleteusers', async function (req, res) {

    if(req.session.user===null || req.session.user===undefined  || req.session.user.role!==roles[1]){
      let errors = []
      errors.push('No tienes permisos para acceder a esta página');
      req.flash('error', errors);
      return res.redirect('/users/login');
    }

    if (req.query.ids) {
      let ids = Array.isArray(req.query.ids) ? req.query.ids : [req.query.ids] ;
      if(ids.includes(req.session.user._id)){
        let errors = []
        errors.push('No puedes eliminarte a ti mismo');
        req.flash('error', errors);
        ids.filter(id => id !== req.session.user._id);
      }else{

        await usersRepository.deleteUser(ids);
        await friendRequestRepository.deleteFriendRequestByUser(ids);
        await friendshipRepository.deleteFriendshipByUser(ids);
        return res.redirect('/listAllUsers');
      }
    }
    else {
      let errors = []
      errors.push('No hay usuarios seleccionados');
      req.flash('error', errors);
    }
    res.redirect('/listAllUsers');
  });

  async function validateDataEdit(req){
    console.log('entra validar edit datos');
    let errors = [];

    // Validación de los datos del formulario
    if (!req.body.name || req.body.name.length < 3 || req.body.name.trim().length===0) {
      errors.push('El nombre debe tener al menos 3 caracteres');
    }
    if (!req.body.surname || req.body.surname.length < 3 || req.body.surname.trim().length===0) {
      errors.push('Los apellidos deben tener al menos 3 caracteres');
    }

    if(!req.body.role || req.body.role.trim().length===0 || (req.body.role!==roles[0] && req.body.role!==roles[1])){
      errors.push('El rol debe ser válido');
    }

    //para comprobar el email primero tienes que comprobar que se ha cambiado es decir que el email que tienes en la bd
    //para ese id no es el mismo que ya existia
    try {
      let user = await usersRepository.findUser({_id: new ObjectId(req.params._id)}, {});
      if(user.email !== req.body.email){
        if (!req.body.email || !req.body.email.includes('@')|| req.body.email.trim().length===0 ) {
          errors.push('El email debe ser válido');
        }
        //comprobar si el email ya esta registrado llamando a la funcion validate
        let emailExists = await validateIfEmailAlreadyExists(req, usersRepository, errors);
        /*
          ya lo hace el metodo
        if(emailExists){
          errors.push('El email ya está registrado');
        }*/
      }
    } catch(error){
      console.log("error inesperado buscando al usuario ");
    }

    //devolver los errores
    if (errors.length > 0) {
      console.log('errores encontrados en el registro', errors);
      req.flash('error', errors);
    }

    return errors.length > 0;


  }

  /**
   * Comprueba si el email es usado por otro usuario
   * @param req
   * @param usersRepository
   * @param errors
   * @returns {Promise<boolean>}
   */
  async function validateIfEmailAlreadyExists(req, usersRepository, errors) {
    let filter = {email: req.body.email};
    let options = {};
    try {
      let user = await usersRepository.findUser(filter, options);
      if (user !== null && user !== undefined) {
        errors.push('El email ya está registrado');
      }
    } catch (e) {
      errors.push('Error al buscar el email en la base de datos');
    }
    if (errors.length > 0) {
      console.log('errores encontrados en el registro', errors);
      req.flash('error', errors);
    }

    return errors.length > 0;
  }

  /**
   * Lista de usuarios del admin
   */
  app.get('/listAllUsers',requireRole("admin"), function (req, res, next) {
    //comprobar que te viene el parametro sino ponerle el por defecto
    if(req.query.page===undefined || req.query.page===null){
      req.query.page=1;
    }
    let limit=app.get('maxSizePerPage')
    usersRepository.findAllUsers(req.query.page, limit).then(users => {
      usersRepository.countAllUsers().then(totalUsers => {
        let pageCount = Math.ceil(totalUsers / limit);
        res.render('user/list.twig', {
          users: users,
          userRole: req.session.user.role,
          pageCount: pageCount,
          itemCount: totalUsers,
          pages: app.get('paginate').getArrayPages(req)(3, pageCount, req.query.page)
        });
      }).catch(next);
    }).catch(next);


  });



  /**
   * Lista de usuarios estandars, muestra todos los usuarios exceptos los administradores y asi mismo
   */
  app.get('/listUsersSocialMedia',requireAuth, function (req, res, next) {

    // Comprobar que te viene el parámetro sino ponerle el por defecto
    if(req.query.page===undefined || req.query.page===null){
      req.query.page=1;
    }

    var filter = {
      $and: [
        { _id: { $ne: new ObjectId(req.session.user._id) } }, // Excluir al usuario actual
        { role: { $ne: roles[1] } } // Excluir a los administradores
      ]
    };

    if (req.query.search != null && typeof (req.query.search) != "undefined" && req.query.search != "") {
      var searchFilter ={
        $or: [
          { email: {$regex: ".*" + req.query.search + ".*" } },
          { name: {$regex: ".*" + req.query.search + ".*" } },
          { surname: {$regex: ".*" + req.query.search + ".*" } }
        ]
      }

      filter = {
        $and: [
          filter,
          searchFilter
        ]
      };
    }



    let limit=app.get('maxSizePerPage')
    usersRepository.findFilteredPaginatedUsers(filter, req.query.page, limit).then(users => {
      usersRepository.countFilteredUsers(filter).then(totalUsers => {
        let pageCount = Math.ceil(totalUsers / limit);
        let list = [];
        let filter = { user_id: new ObjectId(req.session.user._id)}
        friendshipRepository.findAllFriendships(filter, {}).then(friends =>{
          let count = 0
          for (let i = 0; i < friends.length; i++) {
            for (let j = 0; j < users.length; j++) {
              if (users[j]._id.toString() === friends[i].friend_id.toString()) {
                users[j].friend = true;
                count++;
                if (count === friends.length) {
                  break;
                }
              }
            }
          }
          res.render('user/list.twig', {
            users: users,
            friendshipList: list,
            userRole: req.session.user.role,
            pageCount: pageCount,
            itemCount: totalUsers,
            pages: app.get('paginate').getArrayPages(req)(3, pageCount, req.query.page)
          });
        }).catch(next);
      }).catch(next);
    }).catch(next);
  });



}