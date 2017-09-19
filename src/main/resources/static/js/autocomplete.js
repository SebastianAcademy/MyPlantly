$.ajax({
    url: "/GET",
    type: 'GET',
    dataType: "json",
    success: function (response) {
        $("#tags").autocomplete({
            source: response,

        });
}});








