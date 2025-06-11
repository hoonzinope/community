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

    const userPost = {
        user_seq: null,
        limit : 5,
        offset : 0,
        sortType : "post_seq",
        init: function (userSeq) {
            this.user_seq = userSeq;
            this.getUserPost();
            this.addSelectOption();
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
                    this.setUserPost(data);
                    this.setUserPostPagination(data);
                })
                .catch(error => {
                    console.error("Error fetching user post:", error);
                });
        },
        setUserPost: function (data) {
            console.log(data);
            let postListTbody = document.getElementById("post_list");
            postListTbody.innerHTML = ""; // Clear existing rows

            let postList = data.postList;
            if (postList.length === 0) {
                postListTbody.innerHTML = "<tr><td colspan='5'>작성한 게시글이 없습니다.</td></tr>";
                return;
            }

            postList.forEach((post) => {
                let tr = document.createElement("tr");
                let status = post.delete_flag === "false" ? "삭제" : "활성";
                let statusBadge = post.delete_flag === "false" ? "bg-danger" : "bg-success";
                tr.innerHTML = `
                    <td>${post.title}</td>
                    <td><span class="badge bg-secondary">${post.category}</span></td>
                    <td>${post.view_count}</td>
                    <td>${post.insert_ts.split("T")[0]}</td>
                    <td><span class="badge ${statusBadge}">${status}</td>
                `;
                tr.addEventListener("click", function () {
                    location.href = "/admin/post/detail/" + post.post_seq;
                });
                postListTbody.appendChild(tr);
            });
        },
        setUserPostPagination: function (data) {
            let currentPage = data.currentPage;
            let pageSize = data.pageSize;
            let totalCount = data.totalCount;
            let totalPage = data.totalPage;

            $("#pagination-nav").twbsPagination('destroy');
            $("#pagination-nav").twbsPagination({
                first : null,
                last : null,
                prev : "<i class='ri-arrow-left-line'></i>",
                next : "<i class='ri-arrow-right-line'></i>",
                totalPages: totalPage,
                visiblePages: 5,
                startPage: currentPage,
                initiateStartPageClick: false,
                onPageClick: function (event, page) {
                    userPost.offset = (page - 1) * pageSize;
                    userPost.getUserPost();
                }
            });
        },
        addSelectOption : function() {
            let select = document.getElementById("post_sort_select");
            select.addEventListener("change", function (event) {
                userPost.sortType = event.target.value;
                userPost.offset = 0; // Reset offset to 0 when changing sort type
                userPost.getUserPost();
            })
        }
    }

    const userComment = {
        user_seq: null,
        limit : 5,
        offset : 0,
        sortType : "comment_seq",
        init: function (userSeq) {
            this.user_seq = userSeq;
            this.getUserComment();
            this.addSelectOption();
        },
        getUserComment: function () {
            let url = "/admin/user/readComment";
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
                userComment.setUserComment(data);
                userComment.setUserCommentPagination(data);
            })
            .catch(error => {
                console.error("Error fetching user comment:", error);
            });
        },
        setUserComment : function (data) {
            let commentListDiv = document.getElementById("comment_list");
            commentListDiv.innerHTML = ""; // Clear existing comments

            let commentList = data.commentList;
            if (commentList.length === 0) {
                commentListDiv.innerHTML = "<p>작성한 댓글이 없습니다.</p>";
                return;
            }

            commentList.forEach((comment) => {
                let seq = comment.comment_seq;
                let postTitle = comment.post_title || "알 수 없음";
                let content = comment.content ?
                    (comment.content.length > 30 ? comment.content.substring(0, 30) + "..." : comment.content) : "내용 없음";
                let date = comment.insert_ts ? comment.insert_ts.split("T")[0] : "알 수 없음";

                let commentDiv = document.createElement("div");
                commentDiv.className = "comment-item";
                commentDiv.innerHTML = `
                    <a href="/admin/comments/${seq}" 
                    class="list-group-item list-group-item-action">
                      <div class="d-flex justify-content-between align-items-center">
                        <div>
                          <div class="small text-muted mb-1">게시글: ${postTitle}</div>
                          <div>${content}</div>
                        </div>
                        <div class="text-muted small">${date}</div>
                      </div>
                    </a>
                `;
                commentListDiv.appendChild(commentDiv);
            });
        },
        setUserCommentPagination: function (data) {
            let currentPage = data.currentPage;
            let pageSize = data.pageSize;
            let totalCount = data.totalCount;
            let totalPage = data.totalPage;

            $("#comment-pagination-nav").twbsPagination('destroy');
            $("#comment-pagination-nav").twbsPagination({
                first : null,
                last : null,
                prev : "<i class='ri-arrow-left-line'></i>",
                next : "<i class='ri-arrow-right-line'></i>",
                totalPages: totalPage,
                visiblePages: 5,
                startPage: currentPage,
                initiateStartPageClick: false,
                onPageClick: function (event, page) {
                    userComment.offset = (page - 1) * pageSize;
                    userComment.getUserComment();
                }
            });
        },
        addSelectOption : function() {
            let select = document.getElementById("comment_sort_select");
            select.addEventListener("change", function (event) {
                userComment.sortType = event.target.value;
                userComment.offset = 0; // Reset offset to 0 when changing sort type
                userComment.getUserComment();
            });
        }
    }
})();