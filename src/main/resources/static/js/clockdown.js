
// $(".clock").TimeCircles(
//     { time:
//         { Days: { show: false },
//             Hours: { show: false }}});



$.ajax({
    url: "/GETD",
    type: 'GET',
    dataType: "json",
    success: function (response) {
       console.log(response);

        }});

$(".clock").data('clock', 30).TimeCircles({
    "total_duration" : 30,
    "count_past_zero": false,
    "use_background" : true,
    "time": {
        "Days": {
            "show": false
        },
        "Hours" : {
            "show": false
        },
        "Minutes": {
            "show":false
        }
    }
});

$('.button-block').on('click', function(event) {
    var tmp = $(".clock").TimeCircles().getTime();
    console.log(tmp);
    tmp = tmp + 20;
    $(".clock").data('clock', tmp).TimeCircles({
        "total_duration": tmp
    }).restart();
});


// $(".clock").TimeCircles().addListener(function(unit, value, total) {
//     if(total <= 0) {
//         $(".clock").TimeCircles
//
//     }
// });

function getnextWaterDays() {
    now = new Date();
    now.setSeconds(now.getSeconds() + 15);
    return now;
}



    $(".restart").click(function(){ $(".clock").TimeCircles().restart(); });


