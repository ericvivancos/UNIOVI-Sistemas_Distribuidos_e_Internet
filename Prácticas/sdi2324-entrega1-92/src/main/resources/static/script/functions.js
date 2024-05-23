//Función para eliminar usuarios seleccionados
function deleteSelectedUsers() {
    var usersToDelete = [];
    $("input:checkbox:checked").each(function(){
        usersToDelete.push($(this).attr('id'));
    });
    var urlDelete = '/user/list/delete?userIds=' + usersToDelete.join(",");
    $("#usersTable").load(urlDelete);
}
function deleteLogsByType(){
    var urlUpdate = '/logs/delete';

    var select = document.getElementById('typesCombo');
    var value = select.options[select.selectedIndex].value

    var strType = "?logType=" + value.toString();


    $("#tableLogs").load(urlUpdate+strType);
}