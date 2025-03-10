(function() {
    document.addEventListener('DOMContentLoaded', function(){
        updateUserInfo();
        passwordReset();
        deleteAccount();
    });

    function updateUserInfo() {
        document.getElementById('updateUserInfo').addEventListener('click', function(){
            let confirm = window.confirm('정보를 수정하시겠습니까?');
            if(!confirm){ return false; }
            let data = {
                nickname : document.getElementById('nickname').value,
                email : document.getElementById('email').value
            }
            let endpoint = '/member/me/update';
            fetch(endpoint, {
                method : 'POST',
                headers : {
                    'Content-Type' : 'application/json'
                },
                body : JSON.stringify(data)
            }).then(data => {
                alert("정보가 수정되었습니다.")
                window.location.reload();
            }).catch(error => {
                alert(error);
            })
        })
    }

    function passwordReset() {
        document.getElementById('passwordReset').addEventListener('click', function() {
            let confirm = window.confirm('비밀번호를 초기화 하시겠습니까?');
            if(!confirm){ return false; }
            let endpoint = '/member/me/passwordReset'
            fetch(endpoint)
                .then(response => response.json())
                .then(data => {
                    // redirect password update page
                    alert("비밀번호가 초기화 되었습니다.");
                    window.location.href = "/login";
                })
                .catch(error => {
                    alert(error);
                });
        });
    }

    function deleteAccount() {
        document.getElementById('deleteAccount').addEventListener('click', function() {
            let confirm = window.confirm('계정을 삭제하시겠습니까?');
            if(!confirm) { return false; }
            let endpoint = '/member/me'
            fetch(endpoint, {
                method : "DELETE"
            })
                .then(response => response.json())
                .then(data => {
                    // redirect main page
                    alert("계정이 삭제되었습니다.");
                    window.location.href = "/";
                })
                .catch(error => {
                    alert(error);
                });

        });
    }
})();