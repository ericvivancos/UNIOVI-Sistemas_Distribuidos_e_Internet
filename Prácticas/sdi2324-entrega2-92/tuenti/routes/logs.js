const {requireRole} = require("../middlewares/authentication");
module.exports = function (app,logsRepository) {

    /**
     * Maneja la solicitud para listar registros de logs.
     */
    app.get("/log/list" , requireRole("admin"), (req, res) => {
        let filter = {};
        let selectedLogType = req.query.logType || "" ;
        if (selectedLogType !== "") {
            filter = { type: new RegExp(selectedLogType, "i") };
        }
        let options = {sort: {date: -1} };
        logsRepository.findLogs(filter,options)
            .then((logs) => {
                let response = {
                    logList: logs,
                    selectedLogType: selectedLogType,
                };
                res.render("logs/list.twig",response);
            })
            .catch((error) => {
                console.error("Error al obtener los logs:", error);
                req.flash("error",["Hubo un problema al cargar los logs"]);
                res.redirect("/");
            });
    });
    /**
     * Maneja la solicitud para eliminar registros de logs.
     */
    app.post("/log/delete", requireRole("admin"), (req,res) => {
        let logType = req.body.logType?.trim() || "";
        let filter = {};
        if(logType.length > 0){
            filter = {type: logType};
        }
        logsRepository.deleteLogs(filter)
            .then((result) => {
                req.flash("success",["Se han borrado ${result.deletedCount} logs"]);
                res.redirect(`/log/list?logType=${logType}`);
            })
            .catch((error) => {
                console.error("Error al eliminar logs:",error);
                req.flash("error",["No se pudieron eliminar los logs."]);
                res.redirect("/log/list");
            });
    });
}