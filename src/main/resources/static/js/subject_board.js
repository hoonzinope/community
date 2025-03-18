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
                    subject_board.drawSubjectPosts(selectedSubjectSeq);
                });
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