(function() {
    document.addEventListener('DOMContentLoaded', function(){
        updateUserInfo();
        passwordReset();
        deleteAccount();
        userPost.init();
    });

    function updateUserInfo() {
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
                    'Content-Type' : 'application/json'
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
    }

    function passwordReset() {
        document.getElementById('passwordReset').addEventListener('click', function() {
            let confirm = window.confirm('비밀번호를 초기화 하시겠습니까?');
            if(!confirm){ return false; }
            let endpoint = '/member/me/passwordReset'
            fetch(endpoint,{
                method : "PATCH"
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
                    window.location.href = "/auth/logout";
                })
                .catch(error => {
                    alert(error);
                });

        });
    }

    const userPost = {
        user_seq : user_seq,
        init : function() {
            console.log(this.user_seq);
            if(this.user_seq == null) {
                alert("잘못된 접근입니다.");
                window.location.href = '/';
            }
            this.getUserPost();
        },
        getUserPost : function(offset, limit) {
            let endpoint =
                '/api/post/user?user_seq=' + this.user_seq
                +'&offset=' + (offset ? offset : 0)
                +'&limit=' + (limit ? limit : 10);
            fetch(endpoint)
                .then(response => response.json())
                .then(data => {
                    console.log(data);
                    userPost.drawPost(data);
                    userPost.drawPagination(data.total, offset ? offset : 0, limit ? limit : 10);
                })
                .catch(error => {
                    alert(error);
                });
        },
        drawPost : function(data) {
           let total = data.total;
           let size = data.size;
           let postList = data.postList;
           let postListTbody = document.getElementById('postList');
              postListTbody.innerHTML = '';
              if(size != 0) {
                    for(let i = 0; i < size; i++) {
                        let post = postList[i];
                        let tr = document.createElement('tr');
                        let post_endpoint = '/post/' + post.post_seq;
                        let insert_ts = new Date(post.insert_ts);
                        insert_ts =
                            paddingZero(insert_ts.getMonth()+1)
                            +"-"
                            +paddingZero(insert_ts.getDate())
                            +" "
                            +paddingZero(insert_ts.getHours())
                            +":"
                            +paddingZero(insert_ts.getMinutes());
                        tr.innerHTML =
                            '<td>'+'<span class="badge bg-info">'+post.category+'</span>'+'</td>' +
                            '<td><a href="'+post_endpoint+'" class="text-decoration-none text-dark">'+post.title+'</a></td>' +
                            '<td><i class="ri-eye-line"></i>'+post.view_count+'</td>' +
                            '<td>' + insert_ts + '</td>';
                        postListTbody.appendChild(tr);
                    }
              }
        },
        drawPagination : function(total, offset, limit) {
            let totalPages = Math.ceil(total / limit);
            let currentPage = offset + 1;

            if ($("#pagination").data("twbs-pagination")) {
                $("#pagination").twbsPagination('destroy');
            }

            $("#pagination").twbsPagination({
                first : null,
                last : null,
                totalPages : totalPages,
                visiblePages : 5,
                initiateStartPageClick : false,
                onPageClick : function(event, page) {
                    let offset = (page - 1) * limit;
                    userPost.getUserPost(offset, limit);
                }
            });
        }
    }

    function paddingZero(num) {
        return (num < 10 ? '0' : '') + num;
    }
})();