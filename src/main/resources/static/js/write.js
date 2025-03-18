(function() {
    document.addEventListener("DOMContentLoaded",function() {
        console.log('Hello World');
        loadingSummerNote();
        callSubjects();
        writePost();
    });
    // 현재 에디터에 존재하는 이미지 URL 목록을 저장하는 변수
    let currentImages = [];
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
                },
                onChange: function(contents, $editable) {
                    // 임시 컨테이너를 사용하여 변경된 HTML 내용 내 이미지 태그를 추출
                    var tempDiv = $('<div>').html(contents);
                    var newImages = [];
                    tempDiv.find('img').each(function() {
                        newImages.push($(this).attr('src'));
                    });

                    // 이전 이미지 목록에 있지만, 새 목록에는 없는 이미지가 삭제된 이미지임
                    var deletedImages = currentImages.filter(url => newImages.indexOf(url) === -1);
                    deletedImages.forEach(function(imageUrl){
                        deleteSummernoteFile(imageUrl);
                    });
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
                currentImages.push(imageUrl);
            })
            .catch(error => {
                console.error("이미지 업로드 실패:", error);
            });
    }

    function deleteSummernoteFile(imageUrl) {
        // 서버의 이미지 삭제 API 호출 (fetch를 사용한 예시)
        fetch('/api/image/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
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
    }

    function callSubjects(){
        // fetch('/api/subjects')
        //     .then(function(response) {
        //         response.json().then(function(data) {
        //             drawSubject(data);
        //         });
        //     })
        //     .catch(function(err) {
        //         console.log(err);
        //     });

        const url = '/api/subject/minorSubjects';
        let data = {
            major_seq: category
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
                // console.log(data);
                drawSubject(data);
            })
            .catch(error => {
                console.error('Error fetching subjects:', error);
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
                    history.back();
                });
            }).catch(function(err) {
                console.log(err);
                alert('게시글 작성 실패' + err);
            });
        })
    }
})();