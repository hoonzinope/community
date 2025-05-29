(function() {
    document.addEventListener('DOMContentLoaded', function(e) {
        menu.init();
    });

    const menu = {
        init : function() {
            menu.requestSubjects();
            if (!window.location.pathname.includes('/imgArchive')) {
                menu.searchPosts();
            }
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
                    menu.appendSlideMenu(data.subjectList);
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
                let div_slide_menu = document.createElement('div');
                div_slide_menu.className = 'text-decoration-none d-block collapsed';
                div_slide_menu.setAttribute('data-bs-toggle', 'collapse');
                div_slide_menu.setAttribute('href', '');
                div_slide_menu.setAttribute('role', 'button');
                div_slide_menu.setAttribute('aria-expanded', 'false');
                div_slide_menu.setAttribute('aria-controls', sub_id);
                if(chid_subject_list != undefined && chid_subject_list.length > 0) {
                    div_slide_menu.innerHTML = `${subject_name} <i class="ri-arrow-down-s-line float-end"></i>`;
                }else{
                    div_slide_menu.innerHTML = `${subject_name}`;
                }
                subjectItem.appendChild(div_slide_menu);

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
                        if(child_subject_seq == menu.subject_seq || subject_seq == menu.subject_seq) {
                            li.className = 'active';
                            childSubjectList.className += ' show';
                        }
                        childSubjectList.appendChild(li);

                        li.addEventListener('click', function() {
                            // 클릭된 주제의 게시물 요청
                            menu.requestPostsBySubject(child_subject_seq);
                        })

                    });
                    subjectItem.appendChild(childSubjectList);
                }
                slideMenu.appendChild(subjectItem);

                div_slide_menu.addEventListener('click', function(e) {
                    //e.preventDefault();
                    div_slide_menu.className = 'text-decoration-none d-block' + (div_slide_menu.getAttribute('aria-expanded') === 'true' ? '' : 'collapsed');
                    div_slide_menu.setAttribute('aria-expanded',
                        div_slide_menu.getAttribute('aria-expanded') === 'true' ? 'false' : 'true');

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

            menu.slideMenuToggle();
        },
        requestPostsBySubject : function(subject_seq) {
            window.location = "/board/" + subject_seq;
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