(function() {
    document.addEventListener('DOMContentLoaded', function() {
        imgArchive.init();
        // 스크롤이 바닥에 닿았을 때 추가 이미지 로드
        window.addEventListener('scroll', function() {
            if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
                imgArchive.offset += imgArchive.limit; // 다음 페이지 오프셋 증가
                imgArchive.getImagePosts(); // 추가 이미지 포스트 요청
            }
        });
    });

    let imgArchive = {
        limit : 15,
        offset : 0,
        init: function() {
            this.getImagePosts();
        },
        getImagePosts: function() {
            let url = "/api/image-posts";
            let params = {
                limit: this.limit,
                offset: this.offset
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
                console.log(data);
                if (data.imagePostList !== undefined) {
                    this.drawImagePosts(data.imagePostList);
                } else {
                    console.error('Error fetching image posts:', data.message);
                }
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
                    <div class="img-overlay">
                        <div class="overlay-content">
                            <div class="img-title">${imagePost.post_title}</div>
                            <div class="img-count">+${images.length - 1}</div>
                        </div>
                    </div>
                `;
                imgCard.addEventListener('click', function() {
                    let title = this.getAttribute('data-title');
                    let imgs = JSON.parse(this.getAttribute('data-imgs'));
                    imgArchive.showImageViewer(title, imgs);
                });
                imgMasonry.appendChild(imgCard);
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
            window.onclick = function(e) {
                const modal = document.getElementById('imgModal');
                if (e.target === modal) modal.style.display = 'none';
            }

            window.onclick = function(e) {
                const closeModal = document.getElementsByClassName('close-modal');
                if (e.target === closeModal[0]) {
                    closeImgModal();
                }
            }
        },

    }
})();