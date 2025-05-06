(function() {
    document.addEventListener('DOMContentLoaded', function() {
        const keyword = new URLSearchParams(window.location.search).get('keyword');
        subjectObj.init();
        seen.init();
        postObj.init(keyword);
        commentObj.init(keyword);
    });

    const subjectObj = {
        init : function() {
            subjectObj.requestSubjects();
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
                    subjectObj.appendSubjectMenu(data.subjectList);
                })
                .catch(error => {
                    console.error('주제 요청 중 에러 발생: ', error);
                });
        },
        appendSubjectMenu : function(subjectList) {
            const subjectMenu = document.getElementById('subjectMenu');
            subjectList.forEach(subject => {
                let subjectItem = document.createElement('li');
                subjectItem.className = 'mt-3';
                let subject_name = subject.subject_name;
                let subject_seq = subject.subject_seq;
                let chid_subject_list = subject.child_subject_list;

                let sub_id = `subject_${subject_seq}`;
                let a = document.createElement('a');
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
                    let childSubjectList = document.createElement('ul');
                    childSubjectList.className = 'list-unstyled collapse ps-3 mt-2';
                    childSubjectList.setAttribute('name', 'child_subject_list');
                    childSubjectList.id = sub_id;
                    chid_subject_list.forEach(child_subject => {
                        let child_subject_seq = child_subject.subject_seq;
                        let child_subject_name = child_subject.subject_name;
                        let li = document.createElement('li');
                        li.innerHTML = `<a class="text-decoration-none small">${child_subject_name}</a>`;
                        // if(child_subject_seq == board.subject_seq || subject_seq == board.subject_seq) {
                        //     li.className = 'active';
                        //     childSubjectList.className += ' show';
                        // }
                        childSubjectList.appendChild(li);

                        li.addEventListener('click', function() {
                            // 클릭된 주제의 게시물 요청
                            subjectObj.redirectPostsBySubject(child_subject_seq);
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

                    let childSubjectList = document.getElementsByName('child_subject_list');
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
                    // board.requestPostsBySubject(subject_seq);
                });
            });
        },
        requestPostsBySubject : function(subject_seq) {
            const mainFeed = document.getElementById('mainFeed');
            mainFeed.innerHTML = ''; // 게시물 초기화
            board.offset = 0; // 오프셋 초기화
            board.noMorePosts = false; // 더 이상 게시물이 없음을 초기화
            board.isLoading = false; // 로딩 상태 초기화
            board.subject_seq = subject_seq;
            board.requestPosts(subject_seq);
        },

        // 주제 클릭 시 게시물 요청
        redirectPostsBySubject : function(subject_seq) {
            window.location.href = "/board/" + subject_seq;
        },
    }

    const seen = {
        init : function() {
            seen.requestSeenList();
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
                    ////console.log(data);
                    seen.appendSeenList(data.postList);
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
    }

    const postObj = {
        offset : 0,
        limit : 10,
        keyword : '',
        init : function(keyword) {
            this.keyword = keyword;
            postObj.cleanPosts();
            postObj.requestPosts(this.offset, this.limit, this.keyword );
        },
        cleanPosts : function() {
            const postsDiv = document.getElementById('postsDiv');
            postsDiv.innerHTML = '';
        },
        // 게시물 검색 요청
        requestPosts : function(offset, limit, keyword) {
            const url = `/api/search`;
            let data = {
                offset: offset,
                limit: limit,
                keyword: keyword,
                type : "POST"
            }
            fetch(url,{
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
                    // 게시물 처리
                    postObj.appendPosts(data.post_list);
                    if(data.post_list.length >= this.limit) {
                        postObj.appendMoreButton(this.offset, this.limit);
                    }
                })
                .catch(error => {
                    console.error('게시물 요청 중 에러 발생: ', error);
                });
        },
        // 게시물 처리
        appendPosts : function(postList) {
            const postsDiv = document.getElementById('postsDiv');
            postList.forEach(post => {
                let postCardDiv = document.createElement('div');
                postCardDiv.className = 'post-card';

                let voteCount = post.like_count - post.dislike_count;
                let voteSection = document.createElement('div');
                voteSection.className = 'vote-section';
                voteSection.innerHTML = `
                    <i class="ri-arrow-up-line"></i>
                    <div>${voteCount}</div>
                    <i class="ri-arrow-down-line"></i>
                `;

                let insertDate = new Date(post.insert_ts);
                let formattedDate = utils.timeSince(insertDate);
                let postContentDiv = document.createElement('div');
                postContentDiv.className = 'post-content';
                postContentDiv.innerHTML = `
                    <h5>${post.title}</h5>
                    <div class="post-meta mb-1">
                        <span>@${post.user_nickname}</span> ·
                        <span>${formattedDate}</span> ·
                        <span class="badge bg-light text-dark">${post.category}</span>
                    </div>
                    <p class="text-truncate">
                        <div>${post.content} </div>
                    </p>
                    <a class="text-decoration-none text-primary small">댓글 보기 (${post.comment_count}개)</a>
                `;

                postCardDiv.appendChild(voteSection);
                postCardDiv.appendChild(postContentDiv);

                // 게시물 클릭 시 상세보기 요청
                postCardDiv.addEventListener('click', function() {
                    postObj.redirectPost(post.post_seq);
                });

                postsDiv.appendChild(postCardDiv);
            });
        },
        // 게시물 상세보기 요청
        redirectPost : function(post_seq) {
            localStorage.getItem("seenPostList") == null ? localStorage.setItem("seenPostList", JSON.stringify([])) : null;
            let seenPostList = JSON.parse(localStorage.getItem("seenPostList"));
            if(!seenPostList.includes(post_seq)) {
                if(seenPostList.length >= 5) {
                    while(seenPostList.length >= 5) {
                        seenPostList.shift(); // 가장 오래된 게시물 제거
                    }
                }
                seenPostList.push(post_seq);
                localStorage.setItem("seenPostList", JSON.stringify(seenPostList));
            }
            window.location.href = `/post/${post_seq}`;
        },
        // 더보기 버튼 생성 및 이벤트 등록
        appendMoreButton : function(offset, limit) {
            let postListDiv = document.getElementById('user-posts');
            let moreBtn = document.createElement('button');
            moreBtn.className = 'btn btn-outline-primary d-block mx-auto mt-3';
            moreBtn.setAttribute('style', 'width: 50%;');
            moreBtn.innerHTML = '더보기';
            moreBtn.addEventListener('click', function() {
                postObj.requestPosts(offset+limit, limit);
                postListDiv.removeChild(moreBtn);
            });
            postListDiv.appendChild(moreBtn);
        }
    }

    const commentObj = {
        offset : 0,
        limit : 10,
        keyword : '',
        init : function(keyword) {
            this.keyword = keyword;
            commentObj.cleanComments();
            commentObj.requestComments(this.offset, this.limit, this.keyword );
        },
        cleanComments : function() {
            const commentsDiv = document.getElementById('commentsDiv');
            commentsDiv.innerHTML = '';
        },
        // 댓글 검색 요청
        requestComments : function(offset, limit, keyword) {
            const url = `/api/search`;
            let data = {
                offset: offset,
                limit: limit,
                keyword: keyword,
                type : "COMMENT"
            }
            fetch(url,{
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
                    // 댓글 처리
                    commentObj.appendComments(data.post_comment_list);
                    if(data.post_comment_list.length >= this.limit) {
                        commentObj.appendMoreButton(this.offset, this.limit);
                    }
                })
                .catch(error => {
                    console.error('댓글 요청 중 에러 발생: ', error);
                });
        },
        appendComments : function(commentList) {
            const commentsDiv = document.getElementById('commentsDiv');
            commentList.forEach(comment => {
                let post = comment.post;

                let commentCardDiv = document.createElement('div');
                commentCardDiv.className = 'post-card mb-3';

                let cardbody = document.createElement('div');
                cardbody.className = 'card-body';

                let relatedPostDiv = document.createElement('div');
                relatedPostDiv.className = 'related-post mb-3';

                let postCard = document.createElement('div');
                postCard.className = 'post-card';

                let voteCount = post.like_count - post.dislike_count;
                let voteSection = document.createElement('div');
                voteSection.className = 'vote-section';
                voteSection.innerHTML = `
                <i class="ri-arrow-up-line"></i>
                <div>${voteCount}</div>
                <i class="ri-arrow-down-line"></i>
                `
                let insertDate = new Date(post.insert_ts);
                let formattedDate = utils.timeSince(insertDate);
                let postContentDiv = document.createElement('div');
                postContentDiv.className = 'post-content';
                postContentDiv.innerHTML = `
                    <h5>${post.title}</h5>
                    <div class="post-meta mb-1">
                        <span>@${post.user_nickname}</span> ·
                        <span>${formattedDate}</span> ·
                        <span class="badge bg-light text-dark">${post.category}</span>
                    </div>
                    <p class="text-truncate">
                        <div>${post.content} </div>
                    </p>
                    <a href="/post/${post.post_seq}" class="text-decoration-none text-primary small">댓글 보기</a>
                `;

                postCard.appendChild(voteSection);
                postCard.appendChild(postContentDiv);
                relatedPostDiv.appendChild(postCard);

                let commentContentDiv = document.createElement('div');
                commentContentDiv.className = 'comment-thread mb-2';
                commentContentDiv.innerHTML = `
                    <div class="comment-content">
                        <p class="mb-1">
                            ${comment.content}
                        </p>
                        <small class="text-muted">작성자: @${comment.user_nickname} 작성 시각: ${formattedDate}</small>
                    </div>
                `;

                cardbody.appendChild(relatedPostDiv);
                cardbody.appendChild(commentContentDiv);

                commentCardDiv.appendChild(cardbody);

                commentsDiv.appendChild(commentCardDiv);
            });
        },
        // 더보기 버튼 생성 및 이벤트 등록
        appendMoreButton : function(offset, limit) {
            let commentListDiv = document.getElementById('user-comments');
            let moreBtn = document.createElement('button');
            moreBtn.className = 'btn btn-outline-primary d-block mx-auto mt-3';
            moreBtn.setAttribute('style', 'width: 50%;');
            moreBtn.innerHTML = '더보기';
            moreBtn.addEventListener('click', function() {
                commentObj.requestComments(offset+limit, limit);
                commentListDiv.removeChild(moreBtn);
            });
            commentListDiv.appendChild(moreBtn);
        }
    }
})();