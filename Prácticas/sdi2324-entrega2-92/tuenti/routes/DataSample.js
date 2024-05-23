module.exports = function (app, dataSample) {

    /**
     * Restablece la base de datos borrando los datos existentes e introduciendo datos de prueba.
     */
    app.get('/test/reset', async function (req, res) {
        console.log('resetear bd');
        try {
            await dataSample.deleteAll();
            await dataSample.initializeData();
            res.send('Base de datos reseteada');
        } catch (error) {
            console.error('Error al resetear la base de datos:', error);
            res.status(500).send('Error al resetear la base de datos');
        }
    });

}