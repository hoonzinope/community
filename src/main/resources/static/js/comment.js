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
        if (commentList) {
            fetch(`/api/post/${post_seq}/comments`)
                .then(response => response.json())
                .then(data => {
                    console.log(data);
                    drawComments(data);
                    replyButton();
                    cancelReplyButton();
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

        let p_user_name = comment.p_user_name != "" ? "@" + comment.p_user_name : "";
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
                        <div class="${depth}">
                            <button class="btn btn-link btn-sm reply-btn">Reply</button>
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
                    <div class="col-2 text-end">
                        <i class="ri-thumb-up-line reaction-btn me-1"></i> 5
                        <i class="ri-thumb-down-line reaction-btn ms-2"></i> 1
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
    }

    function cancelReplyButton() {
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
})();