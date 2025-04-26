(function() {
    document.addEventListener("DOMContentLoaded", function() {
        subjectObj.init();
        postObj.init(user_seq);
        commentObj.init(user_seq);
        likeObj.init(user_seq);
        dislikeObj.init(user_seq);
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
                    subjectObj.appendSlideMenu(data.subjectList);
                })
                .catch(error => {
                    console.error('주제 요청 중 에러 발생: ', error);
                });
        },
        // slideMenu 그리기
        appendSlideMenu : function(subjectList) {
            let slideMenu = document.getElementById('slideMenu');
            subjectList.forEach(subject => {
                let subjectItem = document.createElement('li');
                subjectItem.className = 'mt-3';
                let subject_name = subject.subject_name;
                let subject_seq = subject.subject_seq;
                let chid_subject_list = subject.child_subject_list;

                let sub_id = `subject_slide_${subject_seq}`;
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
                    const childSubjectList = document.createElement('ul');
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
                slideMenu.appendChild(subjectItem);

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
                    // subjectObj.requestPostsBySubject(subject_seq);
                });
            });

            subjectObj.slideMenuToggle();
        },
        slideMenuToggle : function() {
            const toggleBtn = document.getElementById("sidebarToggleBtn");
            const drawer = document.getElementById("slideSidebar");
            const backdrop = document.getElementById("sidebarBackdrop");

            function openSidebar() {
                drawer.classList.add("show");
                drawer.classList.remove("d-none");
                backdrop.classList.remove("d-none");
            }

            function closeSidebar() {
                drawer.classList.remove("show");
                setTimeout(() => drawer.classList.add("d-none"), 300); // 애니메이션 후 숨김
                backdrop.classList.add("d-none");
            }

            toggleBtn.addEventListener("click", () => {
                if (drawer.classList.contains("show")) {
                    closeSidebar();
                } else {
                    openSidebar();
                }
            });

            backdrop.addEventListener("click", closeSidebar);
        },
        // 주제 클릭 시 게시물 요청
        redirectPostsBySubject : function(subject_seq) {
            window.location.href = "/board/" + subject_seq;
        },
    }

    const postObj = {
        offset : 0,
        limit : 10,
        user_seq : -1,
        init : function(user_seq) {
            this.user_seq = user_seq;
            postObj.cleanPosts();
            postObj.requestPosts();
        },
        cleanPosts : function() {
            let postListDiv = document.getElementById('user-posts');
            postListDiv.innerHTML = '';
        },
        // 게시물 요청
        requestPosts : function(offset, limit) {
            let url = '/api/post/user/?user_seq=' + this.user_seq;
            url += "&offset=" + (offset ? offset : this.offset);
            url += "&limit=" + (limit ? limit : this.limit);
            fetch(url,{
                method : "GET",
                headers : {
                    'Content-Type' : 'application/json',
                    'Accept': 'application/json'
                },
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답에 문제가 있습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    postObj.appendPosts(data.postList);
                    if(data.postList.length >= this.limit) {
                        postObj.appendMoreButton(this.offset, this.limit);
                    }
                })
                .catch(error => {
                    console.error('게시물 요청 중 에러 발생: ', error);
                });
        },
        appendPosts : function(postList) {
            let postListDiv = document.getElementById('user-posts');
            if(postList.length == 0) {
                let mutedDiv = document.createElement('div');
                mutedDiv.className = 'text-muted';
                mutedDiv.innerHTML = '게시물이 없습니다.';

                postListDiv.appendChild(mutedDiv);
            }

            postList.forEach(post => {
               let postDiv = document.createElement('div');
                postDiv.className = 'card p-3';

                let postTitle = document.createElement('h6');
                postTitle.className = 'card-title';
                postTitle.innerHTML = post.title;

                let insertDate = new Date(post.insert_ts);
                let formattedDate = utils.timeSince(insertDate);
                let postTs = document.createElement('p');
                postTs.className = 'card-text text-muted mb-1';
                postTs.innerHTML = formattedDate;

                let postContent = document.createElement('p');
                postContent.className = 'card-text text-truncate';
                postContent.innerHTML = post.content.length > 10 ? post.content.substring(0, 10) + '...' : post.content;

                    let postLink = document.createElement('a');
                postLink.className = 'text-primary small';
                postLink.href = '/post/' + post.post_seq;
                postLink.innerHTML = "자세히 보기";

                postDiv.appendChild(postTitle);
                postDiv.appendChild(postTs);
                postDiv.appendChild(postContent);
                postDiv.appendChild(postLink);
                postListDiv.appendChild(postDiv);
            });
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
    };

    const commentObj = {
        offset : 0,
        limit : 10,
        user_seq : -1,
        init : function(user_seq) {
            this.user_seq = user_seq;
            commentObj.cleanComments();
            commentObj.requestComments();
        },
        cleanComments : function() {
            let commentListDiv = document.getElementById('user-comments');
            commentListDiv.innerHTML = '';
        },
        // 댓글 요청
        requestComments : function(offset, limit) {
            let url = '/api/comment/user/?user_seq=' + this.user_seq;
            url += "&offset=" + (offset ? offset : this.offset);
            url += "&limit=" + (limit ? limit : this.limit);
            fetch(url,{
                method : "GET",
                headers : {
                    'Content-Type' : 'application/json',
                    'Accept': 'application/json'
                },
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답에 문제가 있습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    commentObj.appendComments(data.commentList);
                    if(data.commentList.length >= this.limit) {
                        commentObj.appendMoreButton(this.offset, this.limit);
                    }
                })
                .catch(error => {
                    console.error('댓글 요청 중 에러 발생: ', error);
                });
        },
        appendComments : function(commentList) {
            let commentListDiv = document.getElementById('user-comments');
            if(commentList.length == 0) {
                let mutedDiv = document.createElement('div');
                mutedDiv.className = 'text-muted';
                mutedDiv.innerHTML = '댓글이 없습니다.';
                commentListDiv.appendChild(mutedDiv);
            }

            commentList.forEach(comment => {
                let commentDiv = document.createElement('div');
                commentDiv.className = 'card p-3';

                let commentContent = document.createElement('p');
                commentContent.className = 'card-text text-truncate';
                commentContent.innerHTML = comment.content.length > 10 ? comment.content.substring(0, 10) + '...' : comment.content;

                let insertDate = new Date(comment.insert_ts);
                let formattedDate = utils.timeSince(insertDate);
                let commentTs = document.createElement('p');
                commentTs.className = 'card-text text-muted mb-1';
                commentTs.innerHTML = formattedDate;

                let postLink = document.createElement('a');
                postLink.className = 'text-primary small';
                postLink.href = '/post/' + comment.post_seq;
                postLink.innerHTML = "자세히 보기";

                commentDiv.appendChild(commentContent);
                commentDiv.appendChild(commentTs);
                commentDiv.appendChild(postLink);
                commentListDiv.appendChild(commentDiv);
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
    };

    const likeObj = {
        offset : 0,
        limit : 10,
        user_seq : -1,
        init : function(user_seq) {
            this.user_seq = user_seq;
            likeObj.cleanLikePosts();
            likeObj.requestLikePosts();
        },
        cleanLikePosts : function() {
            let likeListDiv = document.getElementById('user-upvoted');
            likeListDiv.innerHTML = '';
        },
        // 좋아요 한 게시물 요청
        requestLikePosts : function(offset, limit) {
            let url = '/api/like/user/?user_seq=' + this.user_seq;
            url += "&offset=" + (offset ? offset : this.offset);
            url += "&limit=" + (limit ? limit : this.limit);
            fetch(url,{
                method : "GET",
                headers : {
                    'Content-Type' : 'application/json',
                    'Accept': 'application/json'
                },
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답에 문제가 있습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    likeObj.appendLikePosts(data.postList);
                    if(data.postList.length >= this.limit) {
                        likeObj.appendMoreButton(this.offset, this.limit);
                    }
                })
                .catch(error => {
                    console.error('좋아요 게시물 요청 중 에러 발생: ', error);
                });
        },
        appendLikePosts : function(postList) {
            let likeListDiv = document.getElementById('user-upvoted');
            if(postList.length == 0) {
                let mutedDiv = document.createElement('div');
                mutedDiv.className = 'text-muted';
                mutedDiv.innerHTML = '좋아요 한 게시물이 없습니다.';
                likeListDiv.appendChild(mutedDiv);
            }
            postList.forEach(post => {
                let postDiv = document.createElement('div');
                postDiv.className = 'card p-3';

                let postTitle = document.createElement('h6');
                postTitle.className = 'card-title';
                postTitle.innerHTML = post.title;

                let insertDate = new Date(post.insert_ts);
                let formattedDate = utils.timeSince(insertDate);
                let postTs = document.createElement('p');
                postTs.className = 'card-text text-muted mb-1';
                postTs.innerHTML = formattedDate;

                let postContent = document.createElement('p');
                postContent.className = 'card-text text-truncate';
                postContent.innerHTML = post.content.length > 10 ? post.content.substring(0, 10) + '...' : post.content;

                    let postLink = document.createElement('a');
                postLink.className = 'text-primary small';
                postLink.href = '/post/' + post.post_seq;
                postLink.innerHTML = "자세히 보기";

                postDiv.appendChild(postTitle);
                postDiv.appendChild(postTs);
                postDiv.appendChild(postContent);
                postDiv.appendChild(postLink);
                likeListDiv.appendChild(postDiv);
            });
        },
        // 더보기 버튼 생성 및 이벤트 등록
        appendMoreButton : function(offset, limit) {
            let likeListDiv = document.getElementById('user-upvoted');
            let moreBtn = document.createElement('button');
            moreBtn.className = 'btn btn-outline-primary d-block mx-auto mt-3';
            moreBtn.setAttribute('style', 'width: 50%;');
            moreBtn.innerHTML = '더보기';
            moreBtn.addEventListener('click', function() {
                likeObj.requestLikePosts(offset+limit, limit);
                likeListDiv.removeChild(moreBtn);
            });
            likeListDiv.appendChild(moreBtn);
        }
    };

    const dislikeObj = {
        offset : 0,
        limit : 10,
        user_seq : -1,
        init : function(user_seq) {
            this.user_seq = user_seq;
            dislikeObj.cleanDisLikePosts();
            dislikeObj.requestDislikePosts();
        },
        cleanDisLikePosts : function() {
            let dislikeListDiv = document.getElementById('user-downvoted');
            dislikeListDiv.innerHTML = '';
        },
        // 싫어요 한 게시물 요청
        requestDislikePosts : function(offset, limit) {
            let url = '/api/dislike/user/?user_seq=' + this.user_seq;
            url += "&offset=" + (offset ? offset : this.offset);
            url += "&limit=" + (limit ? limit : this.limit);
            fetch(url,{
                method : "GET",
                headers : {
                    'Content-Type' : 'application/json',
                    'Accept': 'application/json'
                },
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답에 문제가 있습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    dislikeObj.appendDislikePosts(data.postList);
                    if(data.postList.length >= this.limit) {
                        dislikeObj.appendMoreButton(this.offset, this.limit);
                    }
                })
                .catch(error => {
                    console.error('싫어요 게시물 요청 중 에러 발생: ', error);
                });
        },
        appendDislikePosts : function(postList) {
            let dislikeListDiv = document.getElementById('user-downvoted');
            if(postList.length == 0) {
                let mutedDiv = document.createElement('div');
                mutedDiv.className = 'text-muted';
                mutedDiv.innerHTML = '싫어요 한 게시물이 없습니다.';
                dislikeListDiv.appendChild(mutedDiv);
            }
            postList.forEach(post => {
                let postDiv = document.createElement('div');
                postDiv.className = 'card p-3';

                let postTitle = document.createElement('h6');
                postTitle.className = 'card-title';
                postTitle.innerHTML = post.title;

                let insertDate = new Date(post.insert_ts);
                let formattedDate = utils.timeSince(insertDate);
                let postTs = document.createElement('p');
                postTs.className = 'card-text text-muted mb-1';
                postTs.innerHTML = formattedDate;

                let postContent = document.createElement('p');
                postContent.className = 'card-text text-truncate';
                postContent.innerHTML = post.content.length > 10 ? post.content.substring(0, 10) + '...' : post.content;

                    let postLink = document.createElement('a');
                postLink.className = 'text-primary small';
                postLink.href = '/post/' + post.post_seq;
                postLink.innerHTML = "자세히 보기";

                postDiv.appendChild(postTitle);
                postDiv.appendChild(postTs);
                postDiv.appendChild(postContent);
                postDiv.appendChild(postLink);
                dislikeListDiv.appendChild(postDiv);
            });
        },
        // 더보기 버튼 생성 및 이벤트 등록
        appendMoreButton : function(offset, limit) {
            let dislikeListDiv = document.getElementById('user-downvoted');
            let moreBtn = document.createElement('button');
            moreBtn.className = 'btn btn-outline-primary d-block mx-auto mt-3';
            moreBtn.setAttribute('style', 'width: 50%;');
            moreBtn.innerHTML = '더보기';
            moreBtn.addEventListener('click', function() {
                dislikeObj.requestDislikePosts(offset+limit, limit);
                dislikeListDiv.removeChild(moreBtn);
            });
            dislikeListDiv.appendChild(moreBtn);
        }
    };
})();