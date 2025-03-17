(function () {
    document.addEventListener('DOMContentLoaded', function () {
        console.log('DOMContentLoaded');
        subject_board.init();
    });

    const subject_board = {
        init: function () {
            let url = window.location.href;
            let subject_seq = url.substring(url.lastIndexOf('/') + 1);
            subject_board.getSubjectName(subject_seq);
            subject_board.getSubjects(subject_seq);
            subject_board.drawSubjectPosts(subject_seq);
        },
        getSubjectName : function(subject_seq) {
            const url = '/api/subject/name';
            let data = {
                subject_seq: subject_seq
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
                        'Content-Type': 'application/json'
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
                    subject_board.drawNav(data);
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
        drawSubjectPosts : function(subject_seq) {
            let tbody = document.getElementById("rows");
            // get subject post
            let endpoint = '/api/posts?limit=5&subject_seq=' + subject_seq;
            fetch(endpoint)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    subject_board.renderSubjectColumn(tbody, data);
                })
                .catch(error => {
                    console.error('Error fetching subjects:', error);
                });
        },
        renderSubjectColumn : function(tbody, data) {
            console.log(data);
            let total = data.total;
            let size = data.size;
            let posts = data.postList;

            let rows = posts.map(function(post) {
                return subject_board.drawRow(post);
            });
            tbody.innerHTML = `${rows.join('')}`;
        },
        drawRow : function(post) {
            let category =
                `<span class="badge bg-info">${post.category}</span>`;
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
        }
    }
})();