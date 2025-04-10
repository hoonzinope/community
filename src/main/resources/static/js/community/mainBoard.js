(function() {
    document.addEventListener("DOMContentLoaded", function() {
        board.init();
        // 스크롤 이벤트 핸들러 - 스크롤 위치가 문서 하단 근처이면 fetchPosts 호출
        window.addEventListener('scroll', () => {
            const scrollPosition = window.innerHeight + window.pageYOffset;
            const threshold = document.body.offsetHeight - 100; // 100px 정도 여유를 둠
            if (scrollPosition >= threshold) {
                board.requestPosts(board.subject_seq);
            }
        });
    });

    const board = {
        offset : 0,
        limit: 10,
        isLoading : false, // 게시물 로딩 중일 때 제어
        noMorePosts : false, // 더 이상 게시물이 없을 때 제어
        subject_seq : 0,
        init: function() {
            board.requestSubjects();
            board.requestPosts(board.subject_seq);
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
                    board.appendSubjectMenu(data.subjectList);
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
                        li.addEventListener('click', function() {
                            board.requestPosts(child_subject_seq);
                        });
                        childSubjectList.appendChild(li);

                        li.addEventListener('click', function() {
                            // 클릭된 주제의 게시물 요청
                            board.requestPostsBySubject(child_subject_seq);
                        })

                    });
                    subjectItem.appendChild(childSubjectList);
                }
                subjectMenu.appendChild(subjectItem);

                a.addEventListener('click', function(e) {
                    //e.preventDefault();
                    console.log('click', subject_seq);
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
                        document.getElementById(sub_id).classList.add('show');
                    }
                    // 클릭된 주제의 게시물 요청
                    board.requestPostsBySubject(subject_seq);
                });
            });
        },
        // 주제 클릭 시 게시물 요청
        requestPostsBySubject : function(subject_seq) {
            const mainFeed = document.getElementById('mainFeed');
            mainFeed.innerHTML = ''; // 게시물 초기화
            board.offset = 0; // 오프셋 초기화
            board.noMorePosts = false; // 더 이상 게시물이 없음을 초기화
            board.isLoading = false; // 로딩 상태 초기화
            board.subject_seq = subject_seq;
            board.requestPosts(subject_seq);
        },
        // 게시물 요청
        requestPosts : function(subject_seq) {
            // 로딩 중이거나 더 이상 불러올 게시물이 없는 경우 요청 중단
            if (board.isLoading || board.noMorePosts) return;

            board.isLoading = true;
            const url = `/api/posts?offset=${board.offset}&limit=${board.limit}&subject_seq=${subject_seq}`;
            fetch(url)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답에 문제가 있습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    // API 응답 구조에 따라 data 내부의 게시물 배열 처리
                    // 예를 들어, data.posts 배열에 게시물이 담겨있다고 가정
                    if (data.postList && data.postList.length > 0) {
                        board.appendPosts(data.postList);
                        board.offset += board.limit;
                    } else {
                        // 더 이상 게시물이 없음을 표시
                        board.noMorePosts = true;
                    }
                    board.isLoading = false;
                })
                .catch(error => {
                    console.error('게시물 요청 중 에러 발생: ', error);
                    board.isLoading = false;
                });
        },
        appendPosts : function(posts) {
            console.log(posts);
            const mainFeed = document.getElementById('mainFeed');
            posts.forEach(post => {
                const postElement = document.createElement('div');
                postElement.className = 'post-card';

                const voteSection = document.createElement('div');
                voteSection.className = 'vote-section';
                voteSection.innerHTML = `
                    <div class="vote-section">
                        <i class="ri-arrow-up-line"></i>
                        <div>${post.like_count}</div>
                        <i class="ri-arrow-down-line"></i>
                    </div>
                `;
                postElement.appendChild(voteSection);

                const postContent = document.createElement('div');
                postContent.className = 'post-content';
                postContent.innerHTML = `
                    <h5>${post.title}</h5>
                    <div class="post-meta mb-1">
                        <span>${post.user_seq}</span> ·
                        <span>1시간 전</span>
                    </div>
                    <p class="text-truncate">
                        <div>${post.content} </div>
                    </p>
                    <a href="/api/post/${post.post_seq}" class="text-decoration-none text-primary small">댓글 보기</a>
                `
                postElement.appendChild(postContent);

                mainFeed.appendChild(postElement);
            });
        },
    }
})();