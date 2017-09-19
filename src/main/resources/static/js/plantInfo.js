/* SCROLL TO BOTTOM */

$(function() {
    $('.scroll-down').click (function() {
        $('html, body').animate({scrollTop: $('section.ok').offset().top }, 'slow');
        return false;
    });
});

/* SEXY TABS */

$(document).ready(function() {

    (function ($) {
        $('.tab ul.tabs').addClass('active').find('> li:eq(0)').addClass('current');

        $('.tab ul.tabs li a').click(function (g) {
            var tab = $(this).closest('.tab'),
                index = $(this).closest('li').index();

            tab.find('ul.tabs > li').removeClass('current');
            $(this).closest('li').addClass('current');

            tab.find('.tab_content').find('div.tabs_item').not('div.tabs_item:eq(' + index + ')').slideUp();
            tab.find('.tab_content').find('div.tabs_item:eq(' + index + ')').slideDown();

            g.preventDefault();
        } );
    })(jQuery);

});

/* SCROLL TO TOP */

$("a[href='#top']").click(function() {
    $("html, body").animate({ scrollTop: 0 }, "slow");
    return false;
});