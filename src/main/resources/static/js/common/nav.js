(function() {
    document.addEventListener('DOMContentLoaded', function() {
        buttonClick();
    });

    function buttonClick() {
        $("#login").off("click").on("click", function() {
            location.href="/login";
        });

        $("#signup").off("click").on("click", function() {
            location.href="/signup";
        })

        $("#logout").off("click").on("click", function(){
            location.href = '/auth/logout';
        });
    }
})();