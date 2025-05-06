(function() {
    document.addEventListener("DOMContentLoaded", function() {
        postObj.init(user_seq);
        commentObj.init(user_seq);
        likeObj.init(user_seq);
        dislikeObj.init(user_seq);
    });

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