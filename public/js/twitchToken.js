$(document).ready(function() {
    var oauthHash = location.hash.substr(1);
    var accessToken = oauthHash.substr(oauthHash.indexOf('access_token=')).split('&')[0].split('=')[1];

    if (accessToken)
        $("#tokenDisplay").html(accessToken);

    if (!location.hash && !location.search)
        window.location = $("#twitchLink").attr("href");
});