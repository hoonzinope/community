(function() {
    document.addEventListener("DOMContentLoaded",function() {
        console.log('Hello World');
        callSubjects();
        writePost();
    });

    function callSubjects(){
        fetch('/api/subjects')
            .then(function(response) {
                response.json().then(function(data) {
                    drawSubject(data);
                });
            })
            .catch(function(err) {
                console.log(err);
            });
    }

    function drawSubject(data) {
        let subjects = data.subjectList;
        let rows = subjects.map(function(subject) {
            return `<option value="${subject.subject_seq}">${subject.subject_name}</option>`;
        });

        $("#subjects").html(`${rows.join('')}`);
    }

    function writePost(){
        $("#publish").off("click").on("click", function(){
            let data = {
                subject_seq: $("#subjects").val(),
                title: $("#title").val(),
                content: $("#content").val(),
                user_seq: $("#user_seq").val()
            }

            fetch('/api/post', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            }).then(function(response) {
                response.json().then(function(data) {
                    console.log(data);
                    location.href = '/';
                });
            }).catch(function(err) {
                console.log(err);
                alert('게시글 작성 실패' + err);
            });
        })
    }
})();