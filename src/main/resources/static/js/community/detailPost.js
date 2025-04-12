(function() {
    document.addEventListener('DOMContentLoaded', function() {
        let post_seq = window.location.href.split('/').pop();
        postObj.init(post_seq);
    });

    const postObj = {
        subject_seq : 0,
        init : function(post_seq) {
            postObj.requestSubjects();
            postObj.requestPost(post_seq);
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
                    console.log(data);
                    postObj.appendPost(data.post);
                })
                .catch(error => {
                    console.error('게시물 요청 중 에러 발생: ', error);
                });
        },
        // 불러온 게시물 main feed에 표시
        appendPost : function(post) {
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
                    <span>${post.user_seq}</span> ·
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
    }
})();