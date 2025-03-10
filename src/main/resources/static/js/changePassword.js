(function() {
    document.addEventListener("DOMContentLoaded", function() {
        changePassword();
    });

    function changePassword() {
        document.getElementById("changePassword").addEventListener("click", function() {
           let new_pw = document.getElementById("new_pw").val();
           let confirmPassword = document.getElementById("confirm_pw").value;
           if(new_pw !== confirmPassword) {
               alert("비밀번호가 일치하지 않습니다.");
           }

           let data = {
               new_pw : new_pw
           }
           let endpoint = "member/me/passwordUpdate"
            fetch(endpoint, {
                method : "POST",
                headers : {
                    "Content-Type" : "application/json"
                },
                body : JSON.stringify(data)
            })
            .then(response => response.json())
            .then(data => {
                // redirect login page
                alert("비밀번호가 변경되었습니다.");
                window.location.href = "/login";
            })
            .catch(error => {
                alert(error);
            })
        });
    }
})();