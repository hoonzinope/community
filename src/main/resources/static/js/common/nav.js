(function() {
    console.log("nav.js");
    document.addEventListener('DOMContentLoaded', function() {
        logout();
        signup();
    });

    function logout(){
        $("#logout").off("click").on("click", function(){
            location.href = '/auth/logout';
        });
    }

    function signup(){
        $("#signup").off("click").on("click", function(){
            let data = {
                user_name: $("#user_name").val(),
                user_pw: $("#user_pw").val(),
                user_email: $("#user_email").val()
            }

            $.ajax({
                url: '/addUser',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function(data){
                    console.log(data);
                    location.href = '/';
                },
                error: function(err){
                    console.log(err);
                    alert('회원가입 실패' + err);
                }
            });
        });
    }
})();