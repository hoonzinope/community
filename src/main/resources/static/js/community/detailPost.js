(function() {
    document.addEventListener('DOMContentLoaded', function() {
        let post_seq = window.location.href.split('/').pop();
        postObj.init(post_seq);
        commentObj.init(post_seq);

    });

    const postObj = {
        subject_seq : 0,
        init : function(post_seq) {
            postObj.requestSubjects();
            postObj.requestPost(post_seq);
            postObj.requestSeenList();
        },
        // 주제 요청
        requestSubjects : function() {
            const url = '/api/subjects/all';
            fetch(url)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답에 문제가 있습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    postObj.appendSubjectMenu(data.subjectList);
                    //postObj.appendSlideMenu(data.subjectList);
                })
                .catch(error => {
                    console.error('주제 요청 중 에러 발생: ', error);
                });
        },
        appendSubjectMenu : function(subjectList) {
            const subjectMenu = document.getElementById('subjectMenu');
            subjectList.forEach(subject => {
                const subjectItem = document.createElement('li');
                subjectItem.className = 'mt-3';
                let subject_name = subject.subject_name;
                let subject_seq = subject.subject_seq;
                let chid_subject_list = subject.child_subject_list;

                let sub_id = `subject_${subject_seq}`;
                const a = document.createElement('a');
                a.className = 'text-decoration-none d-block collapsed';
                a.setAttribute('data-bs-toggle', 'collapse');
                a.setAttribute('href', '');
                a.setAttribute('role', 'button');
                a.setAttribute('aria-expanded', 'false');
                a.setAttribute('aria-controls', sub_id);
                if(chid_subject_list != undefined && chid_subject_list.length > 0) {
                    a.innerHTML = `${subject_name} <i class="ri-arrow-down-s-line float-end"></i>`;
                }else{
                    a.innerHTML = `${subject_name}`;
                }
                subjectItem.appendChild(a);

                if(chid_subject_list != undefined && chid_subject_list.length > 0) {
                    const childSubjectList = document.createElement('ul');
                    childSubjectList.className = 'list-unstyled collapse ps-3 mt-2';
                    childSubjectList.setAttribute('name', 'child_subject_list');
                    childSubjectList.id = sub_id;
                    chid_subject_list.forEach(child_subject => {
                        let child_subject_seq = child_subject.subject_seq;
                        let child_subject_name = child_subject.subject_name;
                        const li = document.createElement('li');
                        li.innerHTML = `<a class="text-decoration-none small">${child_subject_name}</a>`;
                        if(child_subject_seq == postObj.subject_seq || subject_seq == postObj.subject_seq) {
                            li.className = 'active';
                            childSubjectList.className += ' show';
                        }
                        childSubjectList.appendChild(li);

                        li.addEventListener('click', function() {
                            // 클릭된 주제의 게시물 요청
                            postObj.redirectPostsBySubject(child_subject_seq);
                        })

                    });
                    subjectItem.appendChild(childSubjectList);
                }
                subjectMenu.appendChild(subjectItem);

                a.addEventListener('click', function(e) {
                    //e.preventDefault();
                    a.className = 'text-decoration-none d-block' + (a.getAttribute('aria-expanded') === 'true' ? '' : 'collapsed');
                    a.setAttribute('aria-expanded',
                        a.getAttribute('aria-expanded') === 'true' ? 'false' : 'true');

                    const childSubjectList = document.getElementsByName('child_subject_list');
                    if (childSubjectList) {
                        for (let i = 0; i < childSubjectList.length; i++) {
                            if (childSubjectList[i].id !== sub_id) {
                                childSubjectList[i].classList.remove('show');
                            }
                        }
                        if(document.getElementById(sub_id) != undefined) {
                            document.getElementById(sub_id).classList.add('show');
                        }
                    }
                    // 클릭된 주제의 게시물 요청
                    // postObj.redirectPostsBySubject(subject_seq);
                });
            });
        },
        // 주제 클릭 시 게시물 요청
        redirectPostsBySubject : function(subject_seq) {
            window.location.href = "/board/" + subject_seq;
        },
        // 게시물 클릭 시 main feed에 표시
        requestPost : function(post_seq) {
            const mainFeed = document.getElementById('mainFeed');
            const postCard = document.getElementById('postCard');
            // 기존 게시물 카드 제거
            if (postCard) {
                mainFeed.removeChild(postCard);
            }

            const url = `/api/post/${post_seq}`;
            fetch(url)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답에 문제가 있습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    // 게시물 상세보기 처리
                    postObj.appendPost(data.post);
                })
                .catch(error => {
                    console.error('게시물 요청 중 에러 발생: ', error);
                });
        },
        // 불러온 게시물 main feed에 표시
        appendPost : function(post) {
            //console.log(post);
            const mainFeed = document.getElementById('mainFeed');

            const postElement = document.createElement('div');
            postElement.className = 'post-card';

            // vote section
            postObj.appendVoteSection(postElement, post);
            // let voteCount = post.like_count - post.dislike_count;
            // const voteSection = document.createElement('div');
            // voteSection.className = 'vote-section';
            // voteSection.innerHTML = `
            //     <div class="vote-section">
            //         <i class="ri-arrow-up-line"></i>
            //         <div>${voteCount}</div>
            //         <i class="ri-arrow-down-line"></i>
            //     </div>
            // `;
            // postElement.appendChild(voteSection);

            // post content
            let insertDate = new Date(post.insert_ts);
            let formattedDate = utils.timeSince(insertDate);
            const postContent = document.createElement('div');
            postContent.className = 'post-content';
            postContent.setAttribute('style', 'flex: 1');
            postContent.innerHTML = `<h5>${post.title}</h5>`;
            if(user_seq != undefined && post.user_seq == user_seq) {
                postContent.innerHTML = `
                    <div class="d-flex justify-content-between align-items-start mb-2">
                      <h5>${post.title}</h5>
                      <div class="d-flex gap-2">
                        <a id='editPostBtn' class="btn btn-sm btn-outline-secondary">수정</a>
                        <a id='deletePostBtn' class="btn btn-sm btn-outline-danger">삭제</a>
                      </div>
                    </div>
                `;
            }

            postContent.innerHTML += `
                <div class="post-meta mb-2">
                    <span>${post.user_nickname}</span> ·
                    <span>${formattedDate}</span> ·
                    <span class="badge bg-light text-dark">${post.category}</span>
                </div>
                <div class="mb-4" style="white-space: pre-line;">
                    <div>${post.content} </div>
                </div>
            `;
            postElement.appendChild(postContent);

            mainFeed.insertBefore(postElement, mainFeed.firstChild);
            if(user_seq != undefined && post.user_seq == user_seq) {
                // 수정 버튼 이벤트
                document.getElementById('editPostBtn').addEventListener('click', function () {
                    postObj.editPost(post.post_seq);
                });

                // 삭제 버튼 이벤트
                document.getElementById('deletePostBtn').addEventListener('click', function () {
                    postObj.deletePost(post.post_seq);
                });
            }
        },

        // seenList 요청
        requestSeenList : function() {
            // localStorage에서 seenPost 가져오기
            let seenPostList = JSON.parse(localStorage.getItem("seenPostList"));
            if (seenPostList == null || seenPostList.length == 0) {
                //console.log('seenPost가 없습니다.');
                return;
            }
            // seenPost를 서버에 전송
            let data = {
                'seenPostList' : seenPostList
            }
            const url = '/api/post/seen';
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답에 문제가 있습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    // seenList 처리
                    postObj.appendSeenList(data.postList);
                })
                .catch(error => {
                    console.error('게시물 요청 중 에러 발생: ', error);
                });
        },
        // seenList 처리
        appendSeenList : function(postList) {
            const seenPost = document.getElementById('seenPost');
            postList.reverse().forEach(post => {
                const li = document.createElement('li');
                const a = document.createElement('a');
                a.className = 'text-decoration-none mb-3';
                a.innerHTML = `<span>${post.title}</span>`;
                a.setAttribute('href', `/post/${post.post_seq}`);
                li.appendChild(a);
                seenPost.appendChild(li);
            });
        },
        editPost : function(post_seq) {
            window.location.href = '/modify/' + post_seq;
        },
        deletePost : function(post_seq) {
            let check = confirm("게시물을 삭제하시겠습니까?");
            if(!check) return;
            let endpoint = `/api/post/${post_seq}`;
            fetch(endpoint, {
                method : "DELETE",
                headers : {
                    "Content-Type" : "application/json",
                    "Accept" : "application/json"
                }
            }).then(function(response) {
                response.json().then(function(data) {
                    location.href = '/board';
                });
            }).catch(function(err) {
                //console.log(err);
            });
        },

        // vote section 추가
        appendVoteSection : function(postElement, post) {

            let voteDiv = document.createElement('div');
            voteDiv.className = 'vote-section';
            let iconUp = document.createElement('i');
            iconUp.className = 'ri-arrow-up-line vote-btn';
            iconUp.setAttribute('data-type', 'like');

            let voteCount = post.like_count - post.dislike_count;
            let likeCountDiv = document.createElement('div');
            likeCountDiv.innerText = voteCount;

            let iconDown = document.createElement('i');
            iconDown.className = 'ri-arrow-down-line vote-btn';
            iconDown.setAttribute('data-type', 'dislike');

            voteDiv.appendChild(iconUp);
            voteDiv.appendChild(likeCountDiv);
            voteDiv.appendChild(iconDown);

            postElement.appendChild(voteDiv);

            // 좋아요 클릭 이벤트
            iconUp.addEventListener('click', function() {
                let type = iconUp.getAttribute('data-type');
                if(user_seq != -1) {
                    if (iconUp.classList.contains('active')) {
                        iconUp.classList.remove('active');
                        postObj.deleteLikeType(post.post_seq, user_seq);
                        likeCountDiv.innerText = parseInt(likeCountDiv.innerText) - 1;
                    } else {
                        iconUp.classList.add('active');
                        iconDown.classList.remove('active');
                        postObj.addLikeType(post.post_seq, user_seq, 'LIKE');
                        likeCountDiv.innerText = parseInt(likeCountDiv.innerText) + 1;
                    }
                }else{
                    alert("로그인 후 이용 가능합니다.");
                    return;
                }
            });

            // 싫어요 클릭 이벤트
            iconDown.addEventListener('click', function() {
                let type = iconDown.getAttribute('data-type');
                let count = parseInt(likeCountDiv.innerText);
                if(user_seq != -1) {
                    if(iconDown.classList.contains('active')) {
                        iconDown.classList.remove('active');
                        postObj.deleteLikeType(post.post_seq, user_seq);
                        likeCountDiv.innerText = parseInt(likeCountDiv.innerText) + 1;
                    }
                    else {
                        iconDown.classList.add('active');
                        iconUp.classList.remove('active');
                        postObj.addLikeType(post.post_seq, user_seq, "DISLIKE");
                        likeCountDiv.innerText = parseInt(likeCountDiv.innerText) - 1;
                    }
                }
                else{
                    alert("로그인 후 이용 가능합니다.");
                    return;
                }
            });

            // 게시물 좋아요 클릭여부
            if(user_seq != undefined) {
                postObj.requestLikeType(post.post_seq, user_seq);
            }
        },
        // user post like 조회
        requestLikeType : function(post_seq, user_seq) {
            let data = {
                "user_seq" : user_seq,
                "post_seq" : post_seq
            }
            const url = `/api/like/get`;
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답에 문제가 있습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    if(data.like_type === "LIKE") {
                        document.querySelector('.vote-btn[data-type="like"]').classList.add('active');
                    } else if (data.like_type === "DISLIKE") {
                        document.querySelector('.vote-btn[data-type="dislike"]').classList.add('active');
                    }
                });
        },
        // 게시물 좋아요/싫어요 기능
        addLikeType : function(post_seq, user_seq, type) {
            let data = {
                "user_seq" : user_seq,
                "post_seq" : post_seq,
                "like_type" : type
            }

            fetch("/api/like/add", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(data => {});
        },
        deleteLikeType : function(post_seq, user_seq) {
            let data = {
                "user_seq" : user_seq,
                "post_seq" : post_seq,
            };

            fetch("/api/like/delete", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(data => {});
        },
    }

    const commentObj = {
        post_seq : 0,
        init : function (post_seq) {
            //commentObj.requestComment(post_seq);
            commentObj.post_seq = post_seq;
            commentObj.requestComment(post_seq);
            commentObj.writeNewParentComment(post_seq);
        },
        // 댓글 작성 API
        addCommentApi : async function(content, parent_comment_seq, reply_user_seq, post_seq) {
            const url = `/api/post/${post_seq}/comment`;
            const data = {
                content: content,
                parent_comment_seq: parent_comment_seq,
                reply_user_seq: reply_user_seq
            };
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            if (response.ok) {
                const result = await response.json();
                //console.log(result);
                // 댓글 작성 성공 시 댓글 목록 갱신
                commentObj.requestComment(post_seq);
            } else {
                alert('댓글 작성에 실패했습니다.');
            }
        },
        writeNewParentComment : function(post_seq) {
            document.getElementById('submitCommentBtn').addEventListener('click', async function(e) {
                e.preventDefault();
                const textarea = document.getElementById("newCommentContent");
                const content = textarea.value.trim();
                const parentCommentSeq = textarea.dataset.parentCommentSeq || null;
                const replyToUserSeq = textarea.dataset.replyUserSeq || null;

                if (content.trim() === '') {
                    alert('댓글을 입력하세요.');
                    return;
                }

                commentObj.addCommentApi(content, parentCommentSeq, replyToUserSeq, post_seq);
            })
        },
        // 댓글 요청
        requestComment : function(post_seq) {
            const url = `/api/post/${post_seq}/comments`;
            fetch(url)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답에 문제가 있습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    commentObj.appendComment(data.comment_count, data.comments, post_seq, data.user_comment_click);
                })
                .catch(error => {
                    console.error('댓글 요청 중 에러 발생: ', error);
                });
        },
        appendComment : function(comment_count, commentList, post_seq, user_comment_click) {
            document.getElementById('commentList').innerHTML = '';

            let h5 = document.createElement('h5');
            h5.setAttribute('id', 'comment_cnt');
            h5.innerHTML = "댓글 "+comment_count+"개";
            document.getElementById('commentList').appendChild(h5);
            //console.log(commentList);
            let commentElement = document.createElement('div');
            commentList.forEach(comment => {
                let commentContent = comment.content;
                let commentUserSeq = comment.user_seq;
                let commentUserName = comment.user_name;
                let insert_ts = new Date(comment.insert_ts);
                let formattedDate = utils.timeSince(insert_ts);
                let reply_to_user_name = comment.reply_to_user_name;
                if(comment.parent_comment_seq == 0) {
                    commentElement = document.createElement('div');
                    commentElement.className = 'bg-white p-3 rounded mb-3';

                    let div = `
                    <div class="small text-muted mb-1">${commentUserName} • ${formattedDate}</div>
                    <div class="mb-2">${commentContent}</div>
                    `;
                    commentElement.innerHTML = div;
                    commentObj.drawCommentLikeVote(commentElement, comment, user_comment_click);
                    if(user_seq != undefined && comment.user_seq == user_seq) {
                        commentObj.manipulateCommentByUser(commentElement, comment, user_seq);
                    }

                    let a = document.createElement('a');
                    a.className = 'text-decoration-none text-primary small';
                    a.innerText = '답글 달기';
                    commentElement.appendChild(a);

                    let reply_div = document.createElement('div');
                    reply_div.className = 'reply-form ms-4 ps-3 mt-2';
                    reply_div.setAttribute("style","display:none");

                    let reply_textarea = document.createElement('textarea');
                    reply_textarea.className = 'form-control mb-2';
                    reply_textarea.setAttribute('data-parent-comment-seq', comment.comment_seq);
                    reply_textarea.setAttribute('date-reply-user-seq', commentUserSeq);
                    reply_textarea.setAttribute('name', 'content');
                    reply_textarea.setAttribute('row','2');
                    reply_textarea.setAttribute('placeholder','답글을 입력하세요...');
                    reply_div.appendChild(reply_textarea);

                    let reply_button_div = document.createElement('div');
                    reply_button_div.className = 'text-end'
                    reply_button_div.innerHTML = `
                    <button type="button" class="btn btn-sm btn-outline-primary replySubmitBtn">답글 작성</button>
                    `;
                    reply_div.appendChild(reply_button_div);
                    reply_button_div.querySelector('.replySubmitBtn').addEventListener('click', async function() {
                        const content = reply_textarea.value.trim();
                        const parentCommentSeq = reply_textarea.getAttribute('data-parent-comment-seq');
                        const replyToUserSeq = reply_textarea.getAttribute('date-reply-user-seq');

                        if (content === '') {
                            alert('답글 내용을 입력하세요.');
                            return;
                        }
                        commentObj.addCommentApi(content, parentCommentSeq, replyToUserSeq, post_seq);
                    });

                    commentElement.appendChild(reply_div);

                    a.addEventListener("click", function() {
                        if(reply_div.style.display == "none") {
                            reply_div.style.display = 'block';
                        }else{
                            reply_div.style.display = 'none';
                        }
                    });
                }
                else{
                    let replyElement = document.createElement('div');
                    replyElement.className = 'ms-4 ps-3 border-start border-2 mt-3';
                    let div = `
                    <div class="small text-muted mb-1">${commentUserName} • ${formattedDate}</div>
                    <div class="mb-2">
                        <span class="badge bg-light text-dark">@${reply_to_user_name}</span> 
                        ${commentContent}
                    </div>
                    `;
                    replyElement.innerHTML = div;
                    commentObj.drawCommentLikeVote(replyElement, comment, user_comment_click);
                    if(user_seq != undefined && comment.user_seq == user_seq) {
                        commentObj.manipulateCommentByUser(replyElement, comment, user_seq);
                    }

                    let a = document.createElement('a');
                    a.className = 'text-decoration-none text-primary small';
                    a.innerText = '답글 달기';
                    replyElement.appendChild(a);

                    let reply_div = document.createElement('div');
                    reply_div.className = 'reply-form ms-4 ps-3 mt-2';
                    reply_div.setAttribute("style","display:none");

                    let reply_textarea = document.createElement('textarea');
                    reply_textarea.className = 'form-control mb-2';
                    reply_textarea.setAttribute('data-parent-comment-seq', comment.parent_comment_seq);
                    reply_textarea.setAttribute('date-reply-user-seq', commentUserSeq);
                    reply_textarea.setAttribute('name', 'content');
                    reply_textarea.setAttribute('row','2');
                    reply_textarea.setAttribute('placeholder','답글을 입력하세요...');
                    reply_div.appendChild(reply_textarea);

                    let reply_button_div = document.createElement('div');
                    reply_button_div.className = 'text-end'
                    reply_button_div.innerHTML = `
                    <button type="button" class="btn btn-sm btn-outline-primary replySubmitBtn">답글 작성</button>
                    `;
                    reply_div.appendChild(reply_button_div);
                    reply_button_div.querySelector('.replySubmitBtn').addEventListener('click', async function() {
                        const content = reply_textarea.value.trim();
                        const parentCommentSeq = reply_textarea.getAttribute('data-parent-comment-seq');
                        const replyToUserSeq = reply_textarea.getAttribute('date-reply-user-seq');

                        if (content === '') {
                            alert('답글 내용을 입력하세요.');
                            return;
                        }
                        commentObj.addCommentApi(content, parentCommentSeq, replyToUserSeq, post_seq);
                    });

                    replyElement.appendChild(reply_div);

                    a.addEventListener("click", function() {
                        if(reply_div.style.display == "none") {
                            reply_div.style.display = 'block';
                        }else{
                            reply_div.style.display = 'none';
                        }
                    });
                    commentElement.appendChild(replyElement);
                }

                document.getElementById('commentList').appendChild(commentElement);
            })
        },
        // 댓글 좋아요, 싫어요 기능
        drawCommentLikeVote: function(commentElement, comment, user_comment_click) {
            let voteDiv = document.createElement('div');
            voteDiv.className = 'comment-vote d-flex align-items-center gap-2 text-muted small mt-1';

            let iconUp = document.createElement('i');
            iconUp.className = 'ri-arrow-up-line vote-btn';
            // 댓글 좋아요 클릭 여부
            if(user_comment_click != undefined) {
                let clickInfo = user_comment_click[comment.comment_seq];
                if(clickInfo[0] == true && clickInfo[1] == 1) {
                    iconUp.className = 'ri-arrow-up-line vote-btn active';
                }
            }
            iconUp.setAttribute('data-type', 'like');
            iconUp.setAttribute('data-comment-seq', comment.comment_seq);
            iconUp.setAttribute('style', 'cursor:pointer;');

            let likeCountSpan = document.createElement('span');
            likeCountSpan.className = 'like-count';
            likeCountSpan.innerText = comment.like_cnt;

            let iconDown = document.createElement('i');
            iconDown.className = 'ri-arrow-down-line vote-btn';
            if(user_comment_click != undefined && user_comment_click.length > 0) {
                let clickInfo = user_comment_click[comment.comment_seq];
                if(clickInfo[0] == true && clickInfo[1] == 0) {
                    iconDown.className = 'ri-arrow-down-line vote-btn active';
                }
            }
            iconDown.setAttribute('data-type', 'dislike');
            iconDown.setAttribute('data-comment-seq', comment.comment_seq);
            iconDown.setAttribute('style', 'cursor:pointer;');

            let dislikeCountSpan = document.createElement('span');
            dislikeCountSpan.className = 'dislike-count';
            dislikeCountSpan.innerText = comment.dislike_cnt;

            voteDiv.appendChild(iconUp);
            voteDiv.appendChild(likeCountSpan);
            voteDiv.appendChild(iconDown);
            voteDiv.appendChild(dislikeCountSpan);

            commentElement.appendChild(voteDiv);

            // 좋아요 클릭 이벤트
            iconUp.addEventListener('click', function() {
                let comment_seq = iconUp.getAttribute('data-comment-seq');
                let type = iconUp.getAttribute('data-type');

                if(user_seq != -1) {
                    if (iconUp.classList.contains('active')) {
                        iconUp.classList.remove('active');
                        commentObj.deleteLike(comment.comment_seq, user_seq);
                        likeCountSpan.innerText = parseInt(likeCountSpan.innerText) - 1;
                    } else {
                        iconUp.classList.add('active');
                        iconDown.classList.remove('active');
                        commentObj.addLikeType(comment.comment_seq, user_seq, "LIKE");
                        likeCountSpan.innerText = parseInt(likeCountSpan.innerText) + 1;
                    }
                }else{
                    alert("로그인 후 이용 가능합니다.");
                    return;
                }
            });

            // 싫어요 클릭 이벤트
            iconDown.addEventListener('click', function() {
                let comment_seq = iconDown.getAttribute('data-comment-seq');
                let type = iconDown.getAttribute('data-type');

                if(user_seq != -1) {
                    if(iconDown.classList.contains('active')) {
                        iconDown.classList.remove('active');
                        commentObj.deleteLike(comment.comment_seq, user_seq);
                        dislikeCountSpan.innerText = parseInt(dislikeCountSpan.innerText) - 1;
                    }
                    else {
                        iconDown.classList.add('active');
                        iconUp.classList.remove('active');
                        commentObj
                            .addLikeType(comment.comment_seq, user_seq, "DISLIKE");
                        dislikeCountSpan.innerText = parseInt(dislikeCountSpan.innerText) + 1;
                    }
                }
                else{
                    alert("로그인 후 이용 가능합니다.");
                    return;
                }
            });
        },
        addLikeType : function(comment_seq, user_seq, type) {
            let data = {
                "user_seq" : user_seq,
                "comment_seq" : comment_seq,
                "like_type" : type
            }

            fetch("/api/comment/like", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(data => {});
        },
        deleteLike : function(comment_seq, user_seq) {
            let data = {
                "user_seq" : user_seq,
                "comment_seq" : comment_seq,
            };

            fetch("/api/comment/like/delete", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(data => {});
        },
        // 댓글 수정 및 삭제 기능
        manipulateCommentByUser : function(commentElement, comment, user_seq) {
            let comment_edit_form = document.createElement('div');
            comment_edit_form.className = 'comment-edit-form mb-2 d-none';

                let edit_textarea = document.createElement('textarea');
                edit_textarea.className = 'form-control';
                edit_textarea.setAttribute('rows', '2');
                edit_textarea.innerText = comment.content;

                let edit_button_div = document.createElement('div');
                edit_button_div.className = 'text-end mt-1';

                    let edit_complete_button = document.createElement('button');
                    edit_complete_button.className = 'btn btn-sm btn-outline-primary me-2 comment-update-btn';
                    edit_complete_button.innerText = '수정 완료';

                    let edit_cancel_button = document.createElement('button');
                    edit_cancel_button.className = 'btn btn-sm btn-outline-secondary comment-cancel-btn';
                    edit_cancel_button.innerText = '취소';

                    edit_button_div.appendChild(edit_complete_button);
                    edit_button_div.appendChild(edit_cancel_button);

                comment_edit_form.appendChild(edit_textarea);
                comment_edit_form.appendChild(edit_button_div);

            commentElement.appendChild(comment_edit_form);

            // 수정 취소 버튼 동작
            edit_cancel_button.addEventListener('click', function() {
                comment_edit_form.classList.add('d-none');
                comment_update_div.classList.remove('d-none');
            });

            // 수정 완료 버튼 동작
            edit_complete_button.addEventListener('click', function() {
                let updatedContent = edit_textarea.value.trim();
                if (updatedContent === '') {
                    alert('댓글 내용을 입력하세요.');
                    return;
                }
                //console.log(user_seq, updatedContent, comment.comment_seq);
                // 댓글 수정 API 호출
                //commentObj.updateComment(comment.comment_seq, updatedContent);
                commentObj.update_comment(user_seq, updatedContent, comment.comment_seq);
            });

            // ---------------------------------

            let comment_update_div = document.createElement('div');
            comment_update_div.className = 'text-end small mt-1';

                let update_button = document.createElement('a');
                update_button.className = 'text-secondary me-2';
                update_button.innerText = '수정';

                let delete_button = document.createElement('a');
                delete_button.className = 'text-danger';
                delete_button.innerText = '삭제';

                comment_update_div.appendChild(update_button);
                comment_update_div.appendChild(delete_button);

            commentElement.appendChild(comment_update_div);

            // 수정 클릭시 동작
            update_button.addEventListener('click', function() {
                comment_update_div.classList.add('d-none');
                comment_edit_form.classList.remove('d-none');
            });

            // 삭제 클릭시 동작
            delete_button.addEventListener('click', function() {
                let check = confirm("댓글을 삭제하시겠습니까?");
                if(!check) return;
                //console.log(user_seq, comment.comment_seq);
                // 댓글 삭제 API 호출
                //commentObj.deleteComment(comment.comment_seq);
                commentObj.delete_comment(user_seq, comment.comment_seq);
            });
        },
        update_comment : function(user_seq, content, comment_seq) {
            const data = {
                "user_seq": user_seq,
                "content": content,
                "comment_seq": comment_seq
            };
            fetch(`/api/comment/update/${comment_seq}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(data => {
                    // 댓글 변경 성공 시 댓글 목록 갱신
                    commentObj.requestComment(commentObj.post_seq);
            });
        },
        delete_comment : function(user_seq, comment_seq) {
            const data = {
                "user_seq": user_seq,
                "comment_seq": comment_seq
            };
            fetch(`/api/comment/delete/${comment_seq}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(data => {
                    // 댓글 삭제 성공 시 댓글 목록 갱신
                    commentObj.requestComment(commentObj.post_seq);
            });
        }
    }
})();
