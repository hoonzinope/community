(function() {
    document.addEventListener('DOMContentLoaded', function() {
        imgArchive.init();
        // 스크롤이 바닥에 닿았을 때 추가 이미지 로드
        window.addEventListener('scroll', function() {
            if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
                if (!imgArchive.isLoading) {
                    imgArchive.getImagePosts(); // 추가 이미지 포스트 요청
                }
            }
        });
    });

    let imgArchive = {
        limit : 15, // 한 번에 가져올 이미지 포스트 수
        lastId : null,
        nsfw: 0, // NSFW 필터링 여부 (0: 전체, 1: NSFW)
        isLoading : false,
        mnsry: null,
        init: function() {
            this.category_tab(); // 카테고리 탭 이벤트 초기화
            this.init_mansonry(); // Masonry 초기화
            this.getImagePosts();
        },
        category_tab: function() {
            let tabs = document.getElementsByClassName('nav-item');
            for(let i = 0; i < tabs.length; i++) {
                let tab = tabs[i];
                tab.addEventListener('click', function() {
                    // 선택된 탭 강조
                    document.querySelector('.nav-link.active').classList.remove('active');
                    tab.querySelector('.nav-link').classList.add('active');
                    // NSFW 필터링 설정
                    if(document.querySelector('.nav-link.active').getAttribute("data-category") === 'nsfw') {
                        imgArchive.nsfw = 1;
                    }else{
                        imgArchive.nsfw = 0;
                    }
                    imgArchive.lastId = null; // 새로고침 시 마지막 ID 초기화
                    imgArchive.init_mansonry(); // Masonry 초기화
                    imgArchive.getImagePosts(); // 이미지 포스트 새로 가져오기
                });
            }
        },
        init_mansonry : function() {
            let imgMasonry = document.getElementById('imgMasonry');
            imgMasonry.innerHTML = ''; // 기존 카드 제거
            this.msnry = new Masonry('#imgMasonry', {
                itemSelector: '.img-card',
                columnWidth: '.img-card', // 카드의 기본 너비에 맞게 설정
                gutter: 5,               // 카드 사이의 간격(px)
                percentPosition: true
            });
        },
        getImagePosts: function() {
            let url = "/api/image-posts";
            let params = {
                limit: this.limit,
                lastId: this.lastId,
                nsfw: this.nsfw // NSFW 필터링 여부
            }
            this.isLoading = true;
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(params)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                // console.log(data);
                if (data.imagePostList !== undefined) {
                    // 마지막 ID 업데이트
                    this.lastId = data.lastId;
                    this.drawImagePosts(data.imagePostList);
                    this.prefetchThumbnails(); // 썸네일 미리 적재
                } else {
                    console.error('Error fetching image posts:', data.message);
                }
            })
            .finally(function() {
                imgArchive.isLoading = false; // 로딩 상태 해제
            })
        },
        drawImagePosts: function(imagePostList) {
            let imgMasonry = document.getElementById('imgMasonry');
            /*
            <div class="img-card" data-title="여름 바다"
            data-imgs='["https://picsum.photos/300/390?1","https://picsum.photos/260/470?2","https://picsum.photos/270/210?3","https://picsum.photos/300/420?4"]'>
                <img src="https://picsum.photos/300/390?1" class="card-img" alt="썸네일" />
                <div class="img-overlay">
                    <div class="overlay-content">
                        <div class="img-title">여름 바다</div>
                        <div class="img-count">+3</div>
                    </div>
                </div>
            </div>
             */
            for(let i = 0; i < imagePostList.length; i++) {
                let imagePost = imagePostList[i].imagePost;
                let images = imagePostList[i].images;
                let imageUrls = images.map(img => img.image_url);
                let thumbUrl = images[0].thumb_url;

                let imgCard = document.createElement('div');
                imgCard.className = 'img-card';
                imgCard.setAttribute('data-title', imagePost.post_title);
                imgCard.setAttribute('data-imgs', JSON.stringify(imageUrls));
                imgCard.innerHTML = `
                    <img src="${thumbUrl}" class="card-img" alt="썸네일" />
                    <div class="img">
                        <div class="overlay-content">
                            <div class="img-title">${imagePost.post_title}</div>
                            <div class="img-count">+${images.length}</div>
                        </div>
                    </div>
                `;
                imgCard.addEventListener('click', function() {
                    let title = this.getAttribute('data-title');
                    let imgs = JSON.parse(this.getAttribute('data-imgs'));
                    imgArchive.showImageViewer(title, imgs);
                });
                imgMasonry.appendChild(imgCard);
                imgArchive.msnry.appended(imgCard); // Masonry에 추가
                imagesLoaded('#imgMasonry', function() {
                    imgArchive.msnry.layout(); // 레이아웃 업데이트
                });
            }
        },
        showImageViewer: function(title, imgs) {
            // 이미지 뷰어를 표시하는 로직을 여기에 추가
            // console.log('이미지 뷰어 표시:', title, imgs);
            /*
            // 예시용 더미 댓글 데이터
            let comments = [
                {user: '익명1', text: '좋은 사진이네요!', time: '방금'},
                {user: '익명2', text: '파도가 멋져요.', time: '2분전'},
            ];
            function renderComments() {
                const cl = document.getElementById('commentList');
                cl.innerHTML = comments.map(
                    c => `<li class="comment-item"><span class="meta">${c.user} · ${c.time}</span>${c.text}</li>`
                ).join('');
            }
            function submitComment() {
                const input = document.getElementById('commentInput');
                const val = input.value.trim();
                if(val) {
                    comments.push({user: '익명', text: val, time: '방금'});
                    renderComments();
                    input.value = '';
                }
            }
             */
            // Masonry 카드 클릭시 모달 열기
            document.querySelectorAll('.img-card').forEach(card => {
                card.onclick = () => {
                    const imgs = JSON.parse(card.getAttribute('data-imgs'));
                    document.getElementById('modalTitle').textContent = card.getAttribute('data-title');
                    // 이미지 표시
                    const modalImgs = document.getElementById('modalImages');
                    modalImgs.innerHTML = imgs.map(src =>
                        `<img src="${src}" alt="이미지">`
                    ).join('');
                    document.getElementById('imgModal').style.display = 'block';
                }
            });
            function closeImgModal() {
                document.getElementById('imgModal').style.display = 'none';
            }
            // 모달 바깥 클릭 시 닫기 (선택)
            window.addEventListener('click', function(e) {
                const modal = document.getElementById('imgModal');
                if (e.target === modal) modal.style.display = 'none';
            });

            window.addEventListener('click', function(e) {
                const closeModal = document.getElementsByClassName('close-modal');
                if (e.target === closeModal[0]) {
                    closeImgModal();
                }
            });
        },
        prefetchThumbnails : function() {
            let url = "/api/image-prefetch";
            let params = {
                limit: this.limit,
                lastId: this.lastId
            }
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(params)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    // console.log('썸네일 미리 가져오기 성공');
                } else {
                    // console.error('썸네일 미리 가져오기 실패:', data.message);
                }
            })
            .catch(error => {
                // console.error('썸네일 미리 가져오기 중 오류 발생:', error);
            });
        }
    }
})();