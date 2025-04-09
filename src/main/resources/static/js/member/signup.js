(function() {
    document.addEventListener('DOMContentLoaded', function() {
        signUp();
    });

    function signUp() {
        $("#signup").off("click").on("click", function(){

            let user_pw = $("#user_pw").val();
            let confirmPassword = $("#confirmPassword").val();
            if (user_pw !== confirmPassword) {
                alert("pw doesn't match!");
                return;
            }

            let data = {
                user_name: $("#user_name").val(),
                user_email: $("#user_email").val(),
                user_pw: user_pw,
            }
            $.ajax({
                url: '/auth/signup',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function(data){
                    location.href = '/';
                },
                error: function(err){
                    alert('회원가입 실패');
                }
            });
        });
    }
})();