(function() {
    document.addEventListener('DOMContentLoaded', function() {
        let post_seq = window.location.href.split('/').pop();
        modify.init(post_seq);

        modify.searchPosts();
    });

    const modify = {
        // 현재 에디터에 존재하는 이미지 URL 목록을 저장하는 변수
        currentImages: [],
        init : function (post_seq){
            modify.loadPostData(post_seq);
            modify.patch(post_seq);
        },
        // 주제 요청
        requestSubjects : function(subject_seq) {
            const url = '/api/subjects/all';
            fetch(url)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답에 문제가 있습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    //postObj.appendSubjectMenu(data.subjectList);
                    modify.appendSubject(data.subjectList, subject_seq);
                    modify.appendSlideMenu(data.subjectList);
                })
                .catch(error => {
                    console.error('주제 요청 중 에러 발생: ', error);
                });
        },
        appendSubject : function(subjectList, subject_seq) {
            console.log(subjectList);
            $('#subject').empty();
            $('#subject').append('<option value="">주제를 선택하세요</option>');
            subjectList.forEach(subject => {
                let subjectName = subject.subject_name;
                let subjectSeq = subject.subject_seq;
                let child_subject_list = subject.child_subject_list;
                if(child_subject_list == null || child_subject_list.length == 0){
                    return;
                }
                let optgroup = document.createElement("optgroup");
                optgroup.label = subjectName;
                if(child_subject_list.length > 0){
                    let childSubject = child_subject_list.map(child => {
                        if (subject_seq === child.subject_seq)
                            return `<option value="${child.subject_seq}" selected>${child.subject_name}</option>`;
                        return `<option value="${child.subject_seq}">${child.subject_name}</option>`;
                    }).join('');
                    optgroup.innerHTML = childSubject;
                }
                $('#subject').append(optgroup);
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
                        if(child_subject_seq == modify.subject_seq || subject_seq == modify.subject_seq) {
                            li.className = 'active';
                            childSubjectList.className += ' show';
                        }
                        childSubjectList.appendChild(li);

                        li.addEventListener('click', function() {
                            // 클릭된 주제의 게시물 요청
                            modify.redirectPostsBySubject(child_subject_seq);
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
                    // modify.redirectPostsBySubject(subject_seq);
                });
            });

            modify.slideMenuToggle();
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


        loadPostData : function (post_seq) {
            let endpoint = `/api/post/${post_seq}`;
            fetch(endpoint,{
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            })
                .then(function(response) {
                    response.json().then(function(data) {
                        console.log(data);
                        modify.settingData(data);
                    });
                })
                .catch(function(err) {
                    console.log(err);
                });
        },
        // 불러온 게시물 데이터를 화면에 설정
        settingData : function (data) {
            // category
            let subject_seq = data.post.subject_seq;
            modify.requestSubjects(subject_seq);

            // title
            let title = data.post.title;
            $("#title").val(title);

            // content
            let content = data.post.content;
            modify.loadingSummerNote(content);
        },
        // summernote 에디터 초기화
        loadingSummerNote : function (content){
            $('#content').summernote({
                height: 300,
                minHeight: null,
                maxHeight: null,
                focus: true,
                lang: 'ko-KR',
                placeholder: '내용을 입력하세요.',
                callbacks: {
                    onImageUpload: function(files) {
                        for(let i = 0; i < files.length; i++) {
                            modify.uploadSummernoteImageFile(files[i], this);
                        }
                    },
                    onChange: function(contents, $editable) {
                        // 임시 컨테이너를 사용하여 변경된 HTML 내용 내 이미지 태그를 추출
                        let tempDiv = $('<div>').html(contents);
                        let imgTags = tempDiv.find('img');
                        imgTags.each(function() {
                            let imgSrc = $(this).attr('src');
                            if (imgSrc && !modify.currentImages.includes(imgSrc)) {
                                modify.currentImages.push(imgSrc);
                            }
                        });
                        // summernote 에디터에서 삭제된 이미지 URL 목록을 추출
                        let deletedImages = modify.currentImages.filter(function(image) {
                            return !imgTags.is(`[src="${image}"]`);
                        });
                        // 삭제된 이미지 URL 목록을 summernote 에디터에서 삭제
                        deletedImages.forEach(function(imageUrl) {
                            modify.deleteSummernoteFile(imageUrl);
                        });
                    }
                }
            });
            // 초기 값을 설정
            $('#content').summernote('code', content);
            // 초기 content에 이미지가 있다면, 해당 이미지 URL을 currentImages에 저장
            let tempDiv = $('<div>').html(content);
            tempDiv.find('img').each(function() {
                modify.currentImages.push($(this).attr('src'));
            });
        },
        // summernote 에디터에 이미지 업로드
        uploadSummernoteImageFile : function (file, editor) {
            let data = new FormData();
            data.append("image", file);
            fetch('/api/image/upload', {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    // 'Content-Type': 'application/json' // FormData를 사용하므로 Content-Type을 설정하지 않음
                },
                body: data
            })
                .then(response => response.json())
                .then(data => {
                    // 서버에서 반환한 이미지 URL을 summernote 에디터에 삽입
                    const imageUrl = data.url;
                    $('#content').summernote('insertImage', imageUrl);
                    modify.currentImages.push(imageUrl);
                })
                .catch(error => {
                    console.error("이미지 업로드 실패:", error);
                });
        },
        // summernote 에디터에서 이미지 삭제
        deleteSummernoteFile : function (imageUrl) {
            // 서버의 이미지 삭제 API 호출 (fetch를 사용한 예시)
            fetch('/api/image/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Accept': 'application/json'
                },
                body: 'url=' + encodeURIComponent(imageUrl)
            })
                .then(response => response.json())
                .then(data => {
                    console.log("이미지 삭제 응답:", data);
                })
                .catch(error => {
                    console.error("이미지 삭제 실패:", error);
                });
        },
        patch : function (post_seq) {
            $("#publish").off('click').on('click', function() {
                const title = $('#title').val();
                const content = $('#content').summernote('code');
                const subject_seq = $('#subject').val();
                const user_seq = $('#user_seq').val();

                if(title.trim() == ''){
                    alert('제목을 입력하세요.');
                    return;
                }
                if(content.trim() == ''){
                    alert('내용을 입력하세요.');
                    return;
                }
                if(subject_seq == ''){
                    alert('주제를 선택하세요.');
                    return;
                }

                let data = {
                    title: title,
                    content: content,
                    subject_seq: subject_seq,
                    user_seq: user_seq
                }
                console.log(data);

                let endpoint = `/api/post/${post_seq}`;
                fetch(endpoint, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(data)
                }).then(function(response) {
                    response.json().then(function(data) {
                        location.href = `/post/${post_seq}`;
                    });
                }).catch(function(err) {
                    console.log(err);
                    alert('게시글 작성 실패' + err);
                });
            });

            // if (title === '') {
            //     alert('제목을 입력하세요.');
            //     return;
            // }
            // if (content === '') {
            //     alert('내용을 입력하세요.');
            //     return;
            // }
            //
            // $.ajax({
            //     type: 'POST',
            //     url: '/api/community/write',
            //     data: {
            //         title: title,
            //         content: content,
            //         category: category,
            //         user_seq: user_seq
            //     },
            //     success: function(response) {
            //         alert('게시글이 작성되었습니다.');
            //         window.location.href = '/community';
            //     },
            //     error: function(error) {
            //         console.error('Error writing post:', error);
            //         alert('게시글 작성에 실패했습니다.');
            //     }
            // });
        },

        // search
        searchPosts : function() {
            let searchButton = document.getElementById('search-button');
            let searchInput = document.getElementById('search-input');

            searchButton.addEventListener('click', function() {
                let searchInput = document.getElementById('search-input');
                let searchValue = searchInput.value.trim();
                if (searchValue) {
                    window.location.href = `/search?keyword=${searchValue}`;
                }
            });

            searchInput.addEventListener('keypress', function(event) {
                if (event.key === 'Enter') {
                    event.preventDefault();
                    let searchValue = searchInput.value.trim();
                    if (searchValue) {
                        window.location.href = `/search?keyword=${searchValue}`;
                    }
                }
            });
        },
    }
})();