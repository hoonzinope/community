(function () {
    document.addEventListener("DOMContentLoaded", function () {
        let location_info = location.href.split("/");
        let userSeq = location_info[location_info.length - 1];
        userInfo.init(userSeq);
        userPost.init(userSeq);
        userComment.init(userSeq);
    });

    const userInfo = {
        user_seq: null,
        initialValues: null,
        init: function (userSeq) {
            this.user_seq = userSeq;
            this.getUserInfo();
        },
        getUserInfo: function () {
            let url = "/admin/users/get/" + this.user_seq;
            fetch(url, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error("Network response was not ok.");
                    }
                })
                .then(data => {
                    console.log(data);
                    this.setUserInfo(data);
                    this.checkUserInfoChange();
                })
                .catch(error => {
                    console.error("Error fetching user info:", error);
                });
        },
        setUserInfo: function (user) {
            let user_email = document.getElementById("user_email");
            user_email.value = user.user_email;

            let user_nickname = document.getElementById("user_nickname");
            user_nickname.value = user.user_nickname;

            let user_name = document.getElementById("user_name");
            user_name.value = user.user_name;

            let user_role = document.getElementById("user_role");
            Array.from(user_role.options).forEach(option => {
                if (option.value === user.user_role) {
                    option.selected = true;
                }
            });

            let user_insert_ts = document.getElementById("user_insert_ts");
            user_insert_ts.innerText = user.user_insert_ts.split("T")[0];

            let user_update_ts = document.getElementById("user_update_ts");
            user_update_ts.innerText = user.user_update_ts.split("T")[0];

            let user_status = document.getElementById("user_status");
            user_status.innerText = user.user_status == "ACTIVE" ? "활성" : "비활성";

            let user_forced_password_change = document.getElementById("user_forced_password_change");
            user_forced_password_change.innerText = user.user_forced_password_change == "false" ? "불필요" : "필요";

            // 변경 감지
            userInfo.initialValues = {
                email: document.getElementById("user_email").value,
                nickname: document.getElementById("user_nickname").value,
                name: document.getElementById("user_name").value,
                role: document.getElementById("user_role").value
            };
        },
        checkUserInfoChange: function () {
            const updateButton = document.getElementById("user_update_button");
            updateButton.disabled = true;

            ["user_email", "user_nickname", "user_name", "user_role"].forEach(id => {
                document.getElementById(id).addEventListener("change", () => {
                    const currentValues = {
                        email: document.getElementById("user_email").value,
                        nickname: document.getElementById("user_nickname").value,
                        name: document.getElementById("user_name").value,
                        role: document.getElementById("user_role").value
                    };

                    updateButton.disabled = JSON.stringify(initialValues) === JSON.stringify(currentValues);
                });
            });

            updateButton.addEventListener("click", function () {
                if (confirm("정말로 수정하시겠습니까?")) {
                    userInfo.updateUserInfo();
                } else {
                    updateButton.disabled = true;
                }
            });
        },
        updateUserInfo: function () {
            let user_email = document.getElementById("user_email").value;
            let user_nickname = document.getElementById("user_nickname").value;
            let user_name = document.getElementById("user_name").value;
            let user_role = document.getElementById("user_role").value;


            let url = "/admin/users/update/" + this.user_seq;
            fetch(url, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    user_seq: userInfo.user_seq,
                    user_email: user_email,
                    user_nickname: user_nickname,
                    user_name: user_name,
                    user_role: user_role
                })
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error("Network response was not ok.");
                    }
                })
                .then(data => {
                    console.log(data);
                    alert("사용자 정보가 수정되었습니다.");
                    userInfo.init(userInfo.user_seq);
                })
                .catch(error => {
                    console.error("Error updating user info:", error);
                });
        }
    }


    // TODO : request user post
    const userPost = {
        user_seq: null,
        limit : 5,
        offset : 0,
        sortType : "post_seq",
        init: function (userSeq) {
            this.user_seq = userSeq;
            this.getUserPost();
        },
        getUserPost: function () {
            let url = "/admin/user/readPost";
            fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    user_seq: this.user_seq,
                    limit: this.limit,
                    offset: this.offset,
                    sortType: this.sortType
                })
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error("Network response was not ok.");
                    }
                })
                .then(data => {
                    console.log(data);
                    this.setUserPost(data);
                })
                .catch(error => {
                    console.error("Error fetching user post:", error);
                });
        },
        setUserPost: function (data) {
            // TODO : set user post
        }
    }

    // TODO : request user comment
    const userComment = {
        user_seq: null,
        init: function (userSeq) {
            this.user_seq = userSeq;
        }
    }
})();