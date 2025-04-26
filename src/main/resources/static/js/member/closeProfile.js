(function() {
    document.addEventListener('DOMContentLoaded', function(e) {
        userInfo.init();
        searchObj.searchPosts();
    });

    const searchObj = {
        // search
        searchPosts : function() {
            let searchButton = document.getElementById('search-button');
            let searchInput = document.getElementById('search-input');

            searchButton.addEventListener('click', function() {
                let searchInput = document.getElementById('search-input');
                let searchValue = searchInput.value.trim();
                if (searchValue) {
                    window.location.href = `/search?keyword=${searchValue}`;
                }
            });

            searchInput.addEventListener('keypress', function(event) {
                if (event.key === 'Enter') {
                    event.preventDefault();
                    let searchValue = searchInput.value.trim();
                    if (searchValue) {
                        window.location.href = `/search?keyword=${searchValue}`;
                    }
                }
            });
        },
    }


    const userInfo = {
        init : function() {
            this.updateUserInfo();
            this.passwordReset();
            this.deleteAccount();
        },
        updateUserInfo : function() {
            document.getElementById('updateUserInfo').addEventListener('click', function(){
                let confirm = window.confirm('정보를 수정하시겠습니까?');
                if(!confirm){ return false; }
                let data = {
                    user_nickname : document.getElementById('nickname').value,
                    user_email : document.getElementById('email').value
                }
                fetch("/member/me/update", {
                    method : 'POST',
                    headers : {
                        'Content-Type' : 'application/json',
                        'Accept': 'application/json'
                    },
                    body : JSON.stringify(data)
                }).then(data => {
                    alert("정보가 수정되었습니다.")
                    window.location.reload();
                }).catch(error => {
                    alert(error);
                    window.location.reload();
                })
            })
        },
        passwordReset : function() {
            document.getElementById('passwordReset').addEventListener('click', function() {
                let confirm = window.confirm('비밀번호를 초기화 하시겠습니까?');
                if(!confirm){ return false; }
                let endpoint = '/member/me/passwordReset'
                fetch(endpoint,{
                    method : "PATCH",
                    headers : {
                        'Content-Type' : 'application/json',
                        'Accept': 'application/json'
                    }
                })
                    .then(response => response.json())
                    .then(data => {
                        // redirect password update page
                        alert("비밀번호가 초기화 되었습니다.");
                        window.location.href = "/auth/logout";
                    })
                    .catch(error => {
                        alert(error);
                    });
            });
        },
        deleteAccount : function() {
            document.getElementById('deleteAccount').addEventListener('click', function() {
                let confirm = window.confirm('계정을 삭제하시겠습니까?');
                if(!confirm) { return false; }
                let endpoint = '/member/me'
                fetch(endpoint, {
                    method : "DELETE",
                    headers : {
                        'Content-Type' : 'application/json',
                        'Accept': 'application/json'
                    }
                })
                    .then(response => response.json())
                    .then(data => {
                        // redirect main page
                        alert("계정이 삭제되었습니다.");
                        window.location.href = "/auth/logout";
                    })
                    .catch(error => {
                        alert(error);
                    });

            });
        }
    }

})();