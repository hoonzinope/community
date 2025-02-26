(function() {
    document.addEventListener("DOMContentLoaded",function() {
        console.log('Hello World');
        loadingSummerNote();
        callSubjects();
        writePost();
    });

    function loadingSummerNote(){
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
                        uploadSummernoteImageFile(files[i], this);
                    }
                }
            }
        });
    }

    function uploadSummernoteImageFile(file, editor) {
        let data = new FormData();
        data.append("image", file);
        fetch('/api/image/upload', {
            method: 'POST',
            body: data
        })
            .then(response => response.json())
            .then(data => {
                // 서버에서 반환한 이미지 URL을 summernote 에디터에 삽입
                const imageUrl = data.url;
                $('#content').summernote('insertImage', imageUrl);
            })
            .catch(error => {
                console.error("이미지 업로드 실패:", error);
            });
        // .then(function(response) {
        //     response.json().then(function(data) {
        //         $(editor).summernote('insertImage', data, function($image) {
        //             $image.attr('src', data);
        //         });
        //     });
        // })
        // .catch(function(err) {
        //     console.log(err);
        //     alert('이미지 업로드 실패');
        // });
    }

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
                // content: $("#content").val(),
                content : $("#content").summernote('code'),
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