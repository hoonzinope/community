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
                    postObj.redirectPostsBySubject(subject_seq);
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
            console.log(post);
            const mainFeed = document.getElementById('mainFeed');

            const postElement = document.createElement('div');
            postElement.className = 'post-card';

            // vote section
            let voteCount = post.like_count - post.dislike_count;
            const voteSection = document.createElement('div');
            voteSection.className = 'vote-section';
            voteSection.innerHTML = `
                <div class="vote-section">
                    <i class="ri-arrow-up-line"></i>
                    <div>${voteCount}</div>
                    <i class="ri-arrow-down-line"></i>
                </div>
            `;
            postElement.appendChild(voteSection);

            // post content
            let insertDate = new Date(post.insert_ts);
            let formattedDate = utils.timeSince(insertDate);
            const postContent = document.createElement('div');
            postContent.className = 'post-content';
            postContent.innerHTML = `
                <h5>${post.title}</h5>
                <div class="post-meta mb-1">
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
        },

        // seenList 요청
        requestSeenList : function() {
            // localStorage에서 seenPost 가져오기
            let seenPostList = JSON.parse(localStorage.getItem("seenPostList"));
            if (seenPostList == null || seenPostList.length == 0) {
                console.log('seenPost가 없습니다.');
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
            postList.forEach(post => {
                const li = document.createElement('li');
                const a = document.createElement('a');
                a.className = 'text-decoration-none mb-3';
                a.innerHTML = `<span>${post.title}</span>`;
                a.setAttribute('href', `/post/${post.post_seq}`);
                li.appendChild(a);
                seenPost.appendChild(li);
            });
        },
    }

    const commentObj = {
        init : function (post_seq) {
            //commentObj.requestComment(post_seq);
            commentObj.requestComment(post_seq);
            commentObj.writeNewParentComment(post_seq);
        },
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
                console.log(result);
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
                    commentObj.appendComment(data.comment_count, data.comments, post_seq);
                })
                .catch(error => {
                    console.error('댓글 요청 중 에러 발생: ', error);
                });
        },
        appendComment : function(comment_count, commentList, post_seq) {
            document.getElementById('commentList').innerHTML = '';

            let h5 = document.createElement('h5');
            h5.setAttribute('id', 'comment_cnt');
            h5.innerHTML = "댓글 "+comment_count+"개";
            document.getElementById('commentList').appendChild(h5);

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
                }else{
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
        }
    }
})();
