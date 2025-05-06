(function () {
    document.addEventListener('DOMContentLoaded', function () {
        console.log('DOMContentLoaded');
        subject_board.init();
    });

    const subject_board = {
        init: function () {
            let url = window.location.href;
            let subject_seq = category;
            subject_board.getSubjectName(subject_seq);
            subject_board.getSubjects(subject_seq);
            subject_board.drawSubjectPosts(subject_seq, 5, 0);
            subject_board.search(subject_seq, 0, 5);
        },
        getSubjectName : function(subject_seq) {
            const url = '/api/subject/name';
            let data = {
                subject_seq: subject_seq
            }
            fetch(url,{
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(data)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    console.log(data);
                    let subject_name = data.subject_name;
                    if(data.parent_subject_name !== null) {
                        subject_name = data.parent_subject_name+" > "+data.subject_name;
                    }
                    document.getElementById("subject_name").innerHTML = subject_name;
                })
                .catch(error => {
                    console.error('Error fetching subjects:', error);
                });
        },
        getSubjects : function (subject_seq) {
            const url = '/api/subject/minorSubjects';
            let data = {
                major_seq: subject_seq
            }
            fetch(url,{
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(data)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    subject_board.drawNav(subject_seq,data);
                })
                .catch(error => {
                    console.error('Error fetching subjects:', error);
                });
        },
        drawNav : function(subject_seq, subjects) {
            let subjectList = subjects.subjectList;
            let category = subject_seq;
            const nav = document.querySelector('.nav');
            subjectList.forEach(subject => {
                const li = document.createElement('li');
                //const href = `/board/${category}?subCategory=${subject.subject_seq}`;
                li.classList.add('nav-item');
                li.innerHTML = `<a class="nav-link" href="#">${subject.subject_name}</a>`;
                nav.appendChild(li);
            });

            // if clicked, nav active & draw subject posts
            const navLinks = nav.querySelectorAll('.nav-link');
            navLinks.forEach(link => {
                link.addEventListener('click', function (event) {
                    event.preventDefault();
                    navLinks.forEach(link => link.classList.remove('active'));
                    this.classList.add('active');
                    const selectedSubject = this.textContent;
                    const selectedSubjectSeq = subjectList.find(subject => subject.subject_name === selectedSubject).subject_seq;
                    subject_board.drawSubjectPosts(selectedSubjectSeq, 5, 0);
                });
            });

        },
        drawSubjectPosts : function(subject_seq, limit, offset) {
            let tbody = document.getElementById("rows");
            // get subject post
            let endpoint = '/api/posts?offset='+offset+'&limit='+limit+'&subject_seq=' + subject_seq;
            fetch(endpoint,{
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    subject_board.renderSubjectColumn(tbody, data);
                    subject_board.drawPagination(subject_seq, data, offset, limit);
                })
                .catch(error => {
                    console.error('Error fetching subjects:', error);
                });
        },
        renderSubjectColumn : function(tbody, data) {
            let posts = data.postList;

            let rows = posts.map(function(post) {
                return subject_board.drawRow(post);
            });
            tbody.innerHTML = `${rows.join('')}`;
        },
        drawRow : function(post) {
            let category =
                `<span class="badge bg-info">${post.category}</span>`;
            post.insert_ts = subject_board.convertTime(post.insert_ts);
            return `<tr>
                <td>${category}</td>
                <td><a href="/post/${post.post_seq}" class="text-decoration-none text-dark">${post.title}</a></td>
                <td><i class="ri-eye-line"></i> ${post.view_count}</td>
                <td>${post.insert_ts}</td>
                <td>
                    <i class="ri-thumb-up-line text-primary"></i> ${post.like_count} / 
                    <i class="ri-thumb-down-line text-secondary ms-2"></i> ${post.dislike_count}
                </td>
            </tr>`;
        },
        drawPagination : function(subject_seq, data, offset, limit) {
            let total = data.total;
            let totalPages = Math.ceil(total / limit);
            let currentPage = offset + 1;

            $("#search_pagination_div").hide();
            $("#pagination_div").show();
            $("#pagination").twbsPagination({
                first : null,
                last : null,
                totalPages: totalPages,
                visiblePages: 5,
                startPage: currentPage,
                initiateStartPageClick: false,
                onPageClick: function (event, page) {
                    subject_board.drawSubjectPosts(subject_seq, limit, (page - 1) * limit);
                }
            });
        },
        convertTime : function(timestamp) {
            return utils.convertTime(timestamp);
        },
        search : function(subject_seq, offset, limit) {
            let searchBtn = document.getElementById("searchBtn");
            let tbody = document.getElementById("rows");
            searchBtn.addEventListener("click", function(){
                let searchKeyword = document.getElementById("searchKeyword").value;
                let searchType = document.getElementById('searchType').value;
                let subject_seq = category;
                let endpoint = '/api/search';
                let data = {
                    subject_seq: subject_seq,
                    keyword: searchKeyword,
                    offset: offset != undefined ? offset : 0,
                    limit: limit != undefined ? limit : 5,
                    type: searchType,
                    subject_seq : subject_seq
                }
                console.log(data);
                fetch(endpoint,{
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(data)
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(data => {
                        subject_board.drawSearchBar(searchType, searchKeyword);
                        subject_board.renderSubjectColumn(tbody, data);
                        subject_board.drawSearchPagination(data, subject_seq, offset, limit);
                    })
                    .catch(error => {
                        console.error('Error fetching subjects:', error);
                    });
            });
        },
        drawSearchBar : function(type, keyword) {
            let searchType = document.getElementById("searchType");
            let searchKeyword = document.getElementById("searchKeyword");

            searchType.value = type;
            searchKeyword.value = keyword;
        },
        drawSearchPagination : function(data, subject_seq, offset, limit) {
            let total = data.total;
            let totalPages = Math.ceil(total / limit);
            let currentPage = offset + 1;

            $("#pagination_div").hide();
            $("#search_pagination_div").show();
            $("#search_pagination").twbsPagination({
                first : null,
                last : null,
                totalPages: totalPages,
                visiblePages: 5,
                startPage: currentPage,
                initiateStartPageClick: false,
                onPageClick: function (event, page) {
                    subject_board.drawSubjectPosts(subject_seq, limit, (page - 1) * limit);
                }
            });
        }
    }
})();