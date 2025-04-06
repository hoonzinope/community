(function() {
    document.addEventListener("DOMContentLoaded",function() {
        loadPostData();
        writePost();
    });

    function loadPostData() {
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
                    settingData(data);
                });
            })
            .catch(function(err) {
                console.log(err);
            });
    }

    function settingData(data) {
        // category
        let category_seq = data.post.subject_seq;
        callSubjects(category_seq);

        // title
        let title = data.post.title;
        $("#title").val(title);

        // content
        let content = data.post.content;
        loadingSummerNote(content);
    }


    // 현재 에디터에 존재하는 이미지 URL 목록을 저장하는 변수
    let currentImages = [];
    function loadingSummerNote(content){
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
                    let tempDiv = $('<div>').html(contents);
                    let newImages = [];
                    tempDiv.find('img').each(function() {
                        newImages.push($(this).attr('src'));
                    });
                    if(newImages.length == 0)
                        return;
                    // 이전 이미지 목록에 있지만, 새 목록에는 없는 이미지가 삭제된 이미지임
                    let deletedImages = currentImages.filter(url => newImages.indexOf(url) === -1);
                    deletedImages.forEach(function(imageUrl){
                        deleteSummernoteFile(imageUrl);
                    });
                }
            }
        });
        // 초기 값을 설정
        $('#content').summernote('code', content);
        // 초기 content에 이미지가 있다면, 해당 이미지 URL을 currentImages에 저장
        let tempDiv = $('<div>').html(content);
        tempDiv.find('img').each(function() {
            currentImages.push($(this).attr('src'));
        });
        console.log(currentImages);
    }

    function uploadSummernoteImageFile(file, editor) {
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
    }

    function callSubjects(category_seq){
        const url = `/api/subjects/${category_seq}`;
        fetch(url,{
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        })
            .then(function(response) {
                response.json().then(function(data) {
                    drawSubject(data, category_seq);
                });
            })
            .catch(function(err) {
                console.log(err);
            });
    }

    function drawSubject(data, category_seq) {
        let subjects = data.subjectList;
        let rows = subjects.map(function(subject) {
            if (category_seq === subject.subject_seq)
                return `<option value="${subject.subject_seq}" selected>${subject.subject_name}</option>`;
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
                    location.href = '/';
                });
            }).catch(function(err) {
                console.log(err);
                alert('게시글 작성 실패' + err);
            });
        })
    }
})();