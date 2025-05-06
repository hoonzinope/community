(function() {
    document.addEventListener('DOMContentLoaded', function() {
        write.init();
    });

    const write = {
        currentImages: [],
        init: function() {
            write.requestSubjects();
            write.loadingSummerNote();
            write.publish();
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
                    //postObj.appendSubjectMenu(data.subjectList);
                    write.appendSubject(data.subjectList);
                })
                .catch(error => {
                    console.error('주제 요청 중 에러 발생: ', error);
                });
        },
        appendSubject : function(subjectList) {
            //console.log(subjectList);
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
                        return `<option value="${child.subject_seq}">${child.subject_name}</option>`;
                    }).join('');
                    optgroup.innerHTML = childSubject;
                }
                $('#subject').append(optgroup);
            });
        },

        // 주제 클릭 시 게시물 요청
        redirectPostsBySubject : function(subject_seq) {
            window.location.href = "/board/" + subject_seq;
        },

        loadingSummerNote: function() {
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
                            write.uploadSummernoteImageFile(files[i], this);
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
                        var deletedImages = write.currentImages.filter(url => newImages.indexOf(url) === -1);
                        deletedImages.forEach(function(imageUrl){
                            write.deleteSummernoteFile(imageUrl);
                        });

                        $editable.find('iframe[src^="//"]').each(function(){
                            $(this).attr('src', 'https:' + $(this).attr('src'));
                        });
                    }
                }
            });
        },
        uploadSummernoteImageFile: function(file, editor) {
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
                    write.currentImages.push(imageUrl);
                })
                .catch(error => {
                    console.error('Error uploading image:', error);
                });
        },
        deleteSummernoteFile: function(imageUrl) {
            fetch('/api/image/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: 'url=' + encodeURIComponent(imageUrl)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error deleting image');
                }
            })
            .catch(error => {
                console.error('Error deleting image:', error);
            });
        },
        publish : function () {
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
                //console.log(data);

                fetch('/api/post', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(data)
                }).then(function(response) {
                    response.json().then(function(data) {
                        if (document.referrer) {
                            window.location.href = document.referrer;
                        }else{
                            window.location.href = '/';
                        }
                    });
                }).catch(function(err) {
                    //console.log(err);
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
        }
    }
})();