(function () {
    // document.addEventListener("DOMContentLoaded", function () {
    //     let post_seq = document.getElementById("post_seq").value;
    //     console.log("comment.js", post_seq);
    //
    //     // 댓글 목록 조회
    //     comments();
    //
    //     // Reply 버튼 클릭 시 해당 댓글 항목 내의 reply form을 토글
    //     replyButton();
    //
    //     // Cancel 버튼 클릭 시 reply form 숨김
    //     cancelReplyButton();
    // });

    $(document).ready(function () {
        // let post_seq = document.getElementById("post_seq").value;
        // 댓글 목록 조회
        comments();
    })

    function comments() {
        // 댓글 목록 조회
        const commentList = document.querySelector('.comment-list');
        let post_seq = document.getElementById("post_seq").value;
        let user_seq = document.getElementById("user_seq").value;
        console.log(post_seq, user_seq);
        let endpoint = `/api/post/${post_seq}/comments`;
        if (user_seq != null && user_seq !== "") {
            endpoint += `?user_seq=${user_seq}`;
        }

        if (commentList) {
            fetch(endpoint)
                .then(response => response.json())
                .then(data => {
                    drawComments(data);
                    replyButton();
                    userLike();
                    userDislike();
                    editReply();
                    deleteReply();
                });
        }
    }

    function drawComments(commentsData) {
        // comment size
        let comment_count = commentsData.comment_count;
        document.getElementById("comment_count").innerText = "Comment (" + comment_count + ")";
        // comment list
        let commentListElement = $('.comment-list');
        commentListElement.empty();
        commentsData.comments.forEach(function (comment) {
            const commentItem = drawCommentRow(comment);
            commentListElement.append(commentItem);
        });
    }

    function drawCommentRow(comment) {
        console.log(comment);
        let p_user_name = comment.p_user_name !== "" ? "@" + comment.p_user_name : "";
        let depth = "";
        if (comment.sort_path.split("-").length > 1) {
            let d = comment.sort_path.split("-").length - 1;
            if (d == 1) {
                depth = "ps-3";
            } else if (d > 1) {
                depth = "ps-5";
            }
        } else {
            depth = '';
        }

        let like_class = "ri-thumb-up-line";
        if(comment.click_like == true && comment.click_like_type == 1) {
            like_class = "ri-thumb-up-fill";
        }
        let dislike_class = "ri-thumb-down-line";
        if(comment.click_like == true && comment.click_like_type == 0) {
            dislike_class = "ri-thumb-down-fill";
        }

        // 버튼 HTML: 댓글 작성자인 경우에만 수정, 삭제 버튼을 추가함.
        let ownerButtons = '';
        let user_seq = Number(document.getElementById("user_seq").value);
        if (user_seq != null && user_seq !== "" && comment.user_seq === user_seq) {
            ownerButtons = `
            <button id="edit_reply" class="btn btn-link btn-sm edit-comment-btn">Edit</button>
            <button id="delete_reply" class="btn btn-link btn-sm delete-comment-btn" seq="${comment.comment_seq}">Delete</button>
        `;
        }

        return `
            <div class="comment-item p-3 border-bottom">
                <div class="row align-items-start">
                    <!-- 댓글 내용 영역 (전체 너비 중 10/12) -->
                    <div class="col-10">
                        <div class="comment-content ${depth}">
                            <p class="mb-1">${p_user_name} ${comment.content}</p>
                            <div class="text-muted small">
                                <i class="ri-user-line"></i> ${comment.user_name}
                                <span class="mx-2">|</span>
                                <i class="ri-time-line"></i> ${comment.insert_ts}
                            </div>
                        </div>
                        <!-- 댓글 수정용 입력 폼 (숨김 처리됨) -->
                        <div class="edit-form d-none mt-3">
                            <div class="input-group">
                                <input type="text" class="form-control edit-input" value="${comment.content}">
                                <button class="btn btn-sky update-comment-btn" seq="${comment.comment_seq}">완료</button>
                                <button class="btn btn-secondary cancel-edit-btn">취소</button>
                            </div>
                        </div>
                        <div class="${depth}">
                            <button class="btn btn-link btn-sm reply-btn">Reply</button>
                            ${ownerButtons}
                        </div>
                        <!-- Reply Form (숨김) -->
                        <div class="reply-form d-none mt-3">
                            <div class="input-group">
                                <input type="text" class="form-control" placeholder="Write your reply...">
                                <button class="btn btn-sky comment_post" pcseq="${comment.comment_seq}">Post</button>
                                <button class="btn btn-secondary reply-cancel">Cancel</button>
                            </div>
                        </div>
                    </div>
                    <!-- 반응 아이콘 영역 (전체 너비 중 2/12): 항상 우측 고정 -->
                    <div class="col-2 text-end" comment_seq=${comment.comment_seq}>
                        <i class="${like_class} reaction-btn me-1 like_button"> ${comment.like_cnt}</i>
                        <i class="${dislike_class} reaction-btn ms-2 dislike_button"> ${comment.dislike_cnt}</i>
                    </div>
                </div>
            </div>
        `;
    }


    function replyButton() {
        // Reply 버튼 클릭 시 해당 댓글 항목 내의 reply form을 토글
        const replyButtons = document.querySelectorAll('.reply-btn');
        replyButtons.forEach(function (button) {
            button.addEventListener('click', function () {
                const commentItem = this.closest('.comment-item');
                if (commentItem) {
                    const replyForm = commentItem.querySelector('.reply-form');
                    if (replyForm) {
                        replyForm.classList.toggle('d-none');
                    }
                }
            });
        });

        // 댓글 등록
        const comment_post = document.querySelectorAll('.comment_post');
        comment_post.forEach(function (button) {
            button.addEventListener('click', function () {
                const parent_comment_seq = this.getAttribute('pcseq');
                const content = this.closest('.input-group').querySelector('input').value;
                const post_seq = document.getElementById("post_seq").value;
                const user_seq = document.getElementById("user_seq").value;
                if(user_seq == null || user_seq == ""){
                    alert("로그인이 필요합니다.");
                    return;
                }
                const data = {
                    "user_seq": user_seq,
                    "content": content,
                    "parent_comment_seq": parent_comment_seq
                };
                console.log(data);
                fetch(`/api/post/${post_seq}/comment`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data)
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log(data);
                        comments();
                    });
            });
        });

        // Cancel 버튼 클릭 시 reply form 숨김
        // Cancel 버튼 클릭 시 reply form 숨김
        const cancelButtons = document.querySelectorAll('.reply-cancel');
        cancelButtons.forEach(function (button) {
            button.addEventListener('click', function () {
                const replyForm = this.closest('.reply-form');
                if (replyForm) {
                    replyForm.classList.add('d-none');
                }
            });
        });
    }

    // user like click
    function userLike() {
        const like_buttons = document.querySelectorAll('.like_button');
        like_buttons.forEach(function (button) {
            button.addEventListener('click', function () {
                let user_seq = document.getElementById("user_seq").value;
                if(user_seq == null || user_seq === "") {
                    alert("로그인이 필요한 서비스입니다.");
                    return;
                }

                let comment_seq = this.parentElement.getAttribute('comment_seq');
                let like_cnt = this.innerText;
                let dislike_cnt = this.nextElementSibling.innerText;
                if(this.classList.contains("ri-thumb-up-fill")) {
                    this.classList.remove("ri-thumb-up-fill");
                    _deleteLikeType(comment_seq);
                    like_cnt = parseInt(like_cnt) - 1;
                    this.innerText = " "+like_cnt;
                    this.classList.add("ri-thumb-up-line");
                }
                else {
                    if(this.nextElementSibling.classList.contains("ri-thumb-down-fill")) {
                        this.nextElementSibling.classList.remove("ri-thumb-down-fill");
                        _deleteLikeType(comment_seq);
                        dislike_cnt = parseInt(dislike_cnt) - 1;
                        this.nextElementSibling.innerText = " "+dislike_cnt;
                        this.nextElementSibling.classList.add("ri-thumb-down-line");
                    }

                    this.classList.remove("ri-thumb-up-line");
                    this.classList.add("ri-thumb-up-fill");
                    _insertLikeType("LIKE", comment_seq);
                    like_cnt = parseInt(like_cnt) + 1;
                    this.innerText = " "+like_cnt;
                }
            });
        });
    }

    // user dislike click
    function userDislike() {
        const dislike_buttons = document.querySelectorAll('.dislike_button');
        dislike_buttons.forEach(function (button) {
            button.addEventListener('click', function () {
                let user_seq = document.getElementById("user_seq").value;
                if(user_seq == null || user_seq === "") {
                    alert("로그인이 필요한 서비스입니다.");
                    return;
                }

                let comment_seq = this.parentElement.getAttribute('comment_seq');
                let like_cnt = this.previousElementSibling.innerText;
                let dislike_cnt = this.innerText;
                if(this.classList.contains("ri-thumb-down-fill")) {
                    this.classList.remove("ri-thumb-down-fill");
                    _deleteLikeType(comment_seq);
                    dislike_cnt = parseInt(dislike_cnt) - 1;
                    this.innerText = " "+dislike_cnt;
                    this.classList.add("ri-thumb-down-line");
                }
                else {
                    if(this.previousElementSibling.classList.contains("ri-thumb-up-fill")) {
                        this.previousElementSibling.classList.remove("ri-thumb-up-fill");
                        _deleteLikeType(comment_seq);
                        like_cnt = parseInt(like_cnt) - 1;
                        this.previousElementSibling.innerText = " "+like_cnt;
                        this.previousElementSibling.classList.add("ri-thumb-up-line");
                    }

                    this.classList.remove("ri-thumb-down-line");
                    this.classList.add("ri-thumb-down-fill");
                    _insertLikeType("DISLIKE", comment_seq);
                    dislike_cnt = parseInt(dislike_cnt) + 1;
                    this.innerText = " "+dislike_cnt;
                }
            });
        });
    }

    function _insertLikeType(type, comment_seq) {
        let data = {
            "comment_seq": comment_seq,
            "user_seq": document.getElementById("user_seq").value,
            "like_type": type
        }
        console.log(data);
        fetch("/api/comment/like", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(data => {
                console.log(data);
            });
    }

    function _deleteLikeType(comment_seq) {

        let data = {
            "comment_seq": comment_seq,
            "user_seq": document.getElementById("user_seq").value
        }
        console.log(data);
        fetch("/api/comment/like/delete", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(data => {
                console.log(data);
            });
    }

    // reply edit
    function editReply() {
        // Reply 버튼 클릭 시 해당 댓글 항목 내의 reply form을 토글
        const editReplyButtons = document.querySelectorAll('.edit-comment-btn');
        editReplyButtons.forEach(function (button) {
            button.addEventListener('click', function () {
                const commentItem = this.closest('.comment-item');
                if (commentItem) {
                    const replyForm = commentItem.querySelector('.edit-form');
                    if (replyForm) {
                        replyForm.classList.toggle('d-none');
                    }
                }
            });
        });

        _update_reply();
        _cancel_edit_reply();
    }

    function _update_reply() {
        // 댓글 수정
        const comment_post = document.querySelectorAll('.update-comment-btn');
        comment_post.forEach(function (button) {
            button.addEventListener('click', function () {
                const comment_seq = this.getAttribute('seq');
                const content = this.closest('.input-group').querySelector('input').value;
                const user_seq = document.getElementById("user_seq").value;
                if(user_seq == null || user_seq == ""){
                    alert("로그인이 필요합니다.");
                    return;
                }
                const data = {
                    "user_seq": user_seq,
                    "content": content,
                    "comment_seq": comment_seq
                };
                console.log(data);
                fetch(`/api/comment/update/${comment_seq}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data)
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log(data);
                        comments();
                    });
            });
        });
    }

    function _cancel_edit_reply() {
        // Cancel 버튼 클릭 시 reply form 숨김
        const cancelButtons = document.querySelectorAll('.cancel-edit-btn');
        cancelButtons.forEach(function (button) {
            button.addEventListener('click', function () {
                const replyForm = this.closest('.edit-form');
                if (replyForm) {
                    replyForm.classList.add('d-none');
                }
            });
        });
    }

    // reply delete
    function deleteReply() {
        // 댓글 삭제
        const comment_post = document.querySelectorAll('.delete-comment-btn');
        comment_post.forEach(function (button) {
            button.addEventListener('click', function () {
                const comment_seq = this.getAttribute('seq');
                const user_seq = document.getElementById("user_seq").value;
                if(user_seq == null || user_seq == ""){
                    alert("로그인이 필요합니다.");
                    return;
                }
                const data = {
                    "user_seq": user_seq,
                    "comment_seq": comment_seq
                };
                console.log(data);
                fetch(`/api/comment/delete/${comment_seq}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data)
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log(data);
                        comments();
                    });
            });
        });
    }
})();