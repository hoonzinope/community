(function () {
    document.addEventListener('DOMContentLoaded', function () {
        board.init();
    });

    const board = {
        init: function () {
            this.getSubjects();
            this.search();
        },
        getSubjects : function () {
            const url = '/api/subject/majorSubjects';
            fetch(url,
                {
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
                    //board.renderSubjects(data);
                    board.drawNav(data);
                    board.drawSubjectSection(data);
                })
                .catch(error => {
                    console.error('Error fetching subjects:', error);
                });
        },
        drawNav : function(subjects) {
            let subjectList = subjects.subjectList;
            const nav = document.querySelector('.nav');
            subjectList.forEach(subject => {
                const li = document.createElement('li');
                li.classList.add('nav-item');
                li.innerHTML = `<a class="nav-link" href="/board/${subject.subject_seq}">${subject.subject_name}</a>`;
                nav.appendChild(li);
            });
        },
        drawSubjectSection : function(subjectsData) {
            let subjectList = subjectsData.subjectList;
            let subjectlength = subjectList.length;
            let subjectsection = document.querySelector('.subject-container');

            let row = null;
            for(let i = 0; i < subjectlength; i++) {
                let subject_seq = subjectList[i].subject_seq;
                if(i % 2 === 0) {
                    // Create a new row
                    row = document.createElement('div');
                    row.classList.add('row');
                    subjectsection.appendChild(row);
                }

                // Create a new column
                board.drawSubjectColumn(row, subjectList[i], subject_seq);
            }
        },
        drawSubjectColumn : function(row, subject, subject_seq) {

            // get subject post
            let endpoint = '/api/posts?limit=5&subject_seq=' + subject_seq;
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
                    board.renderSubjectColumn(row, subject, data);
                })
                .catch(error => {
                    console.error('Error fetching subjects:', error);
                });
        },
        renderSubjectColumn : function(row, subject, data) {
            let subjectName = subject.subject_name;
            let subject_seq = subject.subject_seq;
            let column = document.createElement('div');
            column.classList.add('col-md-6');
            column.innerHTML = `
                    <div class="subject-section mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="subject-title">${subjectName}</h4>
                        <a href="/board/${subject_seq}" class="btn btn-outline-sky btn-sm">더보기</a>
                    </div>
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <table class="table table-hover mb-0">
                                <thead>
                                <tr>
                                    <th>하위주제</th>
                                    <th>제목</th>
                                    <th>조회수</th>
                                    <th>작성일</th>
                                </tr>
                                </thead>
                                <tbody>
                                ${board.renderSubjectRows(data)}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                `;
            // Append the column to the row
            row.appendChild(column);
        },
        renderSubjectRows : function(data) {
            let total = data.total;
            let postList = data.postList;
            let rows = '';
            postList.forEach(post => {
                post.insert_ts = board.convertTime(post.insert_ts);
                rows += `
                    <tr>
                        <td><span class="badge bg-info">${post.category}</span></td>
                        <td><a href="/post/${post.post_seq}">${post.title}</a></td>
                        <td>${post.view_count}</td>
                        <td>${post.insert_ts}</td>
                    </tr>
                `;
            });
            return rows;
        },
        search : function() {
            const searchButton = document.getElementById("searchBtn");

            searchButton.addEventListener('click', function() {
                // 클릭 시점에 최신 값을 가져옴
                let searchType = document.getElementById('searchType').value;
                let searchInput = document.getElementById('searchKeyword').value;
                searchInput = encodeURIComponent(searchInput);

                const queryString = "?keyword=" + searchInput
                    + "&searchType=" + searchType
                    + "&offset=0&limit=10";

                //console.log("clicked "+ queryString);
                if (searchInput) {
                    window.location.href = '/search' + queryString;
                }
            });

            // Enter 키로도 검색할 수 있게 추가
            document.getElementById('searchKeyword').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    document.getElementById('searchBtn').click();
                }
            });
        },
        convertTime : function(insert_ts) {
            return utils.convertTime(insert_ts);
        }
    }

})();