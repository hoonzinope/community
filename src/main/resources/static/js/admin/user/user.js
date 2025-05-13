(function() {
    document.addEventListener('DOMContentLoaded', function() {
        userinfo.init();
    });

    let userinfo = {
        userRole: null,
        userStatus: null,
        sortType: null,
        limit : 5,
        offset: 0,
        searhType: null,
        searchValue : null,
        init : function() {
            userinfo.getUserList();
        },
        getUserList: function() {
            let url = '/admin/users/get';
            let params = {
                userRole: this.userRole,
                userStatus: this.userStatus,
                sortType: this.sortType,
                limit: this.limit,
                offset: this.offset,
                searchType: this.searchType,
                searchValue: this.searchValue
            };
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(params)
            }).then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Network response was not ok'+ response.json().error_message);
                }
            }).then(data => {
                let userList = data.userList;
                console.log(data);
                console.log(userList);
                this.drawUserCardList(userList);
                this.drawPagination(data.currentPage, data.totalPage, data.pageSize);
            }).catch(error => {
                console.error('There was a problem with the fetch operation:', error);
            });
        },
        drawUserCardList : function (userList) {
            let userCardList = document.getElementById('card-list');
            userCardList.innerHTML = ''; // Clear existing content

            userList.forEach(user => {
                let userCard = document.createElement('div');
                userCard.className = 'user-card';

                let userCardContent = this.drawUserInfo(user);
                userCard.appendChild(userCardContent);

                userCardList.appendChild(userCard);
            });
        },
        drawUserInfo : function (user) {
            let itemDiv = document.createElement('div');
            itemDiv.className = 'd-flex justify-content-between align-items-center';


                let userInfoDiv = document.createElement('div');
                userInfoDiv.className = 'user-info';

                let userInfoHeader = this.drawUserInfoHeader(user, userInfoDiv);
                userInfoDiv.appendChild(userInfoHeader);

                let userDetailsDiv = this.drawUserDetails(user);
                userInfoDiv.innerHTML += userDetailsDiv;

            itemDiv.appendChild(userInfoDiv);

                let userActionDiv = this.drawUserAction(user);
                itemDiv.appendChild(userActionDiv);

            return itemDiv;
        },
        drawUserInfoHeader : function (user) {
            let userInfoHeader = document.createElement('h6');
            userInfoHeader.className = 'mb-1 user-info-header';

            let emailSpan = document.createElement('span');
            emailSpan.className = 'me-2';
            emailSpan.innerText = user.user_email;

            let userRoleSpan = document.createElement('span');
            userRoleSpan.className = 'badge bg-secondary';
            if(user.user_role === 'ADMIN') {
                userRoleSpan.classList.add('badge bg-primary');
            }
            userRoleSpan.innerText = user.user_role;

            let userisBotSpan = document.createElement('span');
            if(user.user_is_bot === true) {
                userisBotSpan.className = 'badge bg-info';
                userisBotSpan.innerText = 'Bot';
            }

            userInfoHeader.appendChild(emailSpan);
            userInfoHeader.appendChild(userRoleSpan);
            userInfoHeader.appendChild(userisBotSpan);
            return userInfoHeader;
        },
        drawUserDetails : function(user) {
            let userDetailsDiv = `
                <div class="text-muted small user-details">
                   <span class="me-2"><i class="ri-user-line"></i> 닉네임: ${user.user_nickname}</span>
                   <span class="me-2"><i class="ri-id-card-line"></i> SEQ: ${user.user_seq}</span>
                   <span class="me-2"><i class="ri-calendar-line"></i> 가입: ${user.user_insert_ts.split('T')[0]}</span>
                   <span class="me-2"><i class="ri-time-line"></i> 수정: ${user.user_update_ts.split('T')[0]}</span>
                </div>
            `;
            return userDetailsDiv;
        },
        drawUserAction : function (user) {
            let userActionDiv = document.createElement('div');
            userActionDiv.className = 'user-actions d-flex';

            let userPwResetButton = document.createElement('button');
            userPwResetButton.className = 'btn btn-warning btn-sm me-2';
            userPwResetButton.title = '비밀번호 초기화';
            userPwResetButton.innerHTML = '<i class="ri-lock-password-line"></i>';
                //'<i class="ri-key-line"></i>';

            userPwResetButton.addEventListener('click', function() {
                // 비밀번호 초기화 로직
                // TODO : 비밀번호 초기화 API 호출
            });
            userActionDiv.appendChild(userPwResetButton);

            let userRoleChangeButton = document.createElement('button');
            userRoleChangeButton.className = 'btn btn-primary btn-sm me-2';
            userRoleChangeButton.title = '권한 변경';
            userRoleChangeButton.innerHTML = '<i class="ri-shield-user-line"></i>';
            userRoleChangeButton.addEventListener('click', function() {
                // 권한 변경 로직
                // TODO : 권한 변경 API 호출
            });
            userActionDiv.appendChild(userRoleChangeButton);

            let userDeleteChangeButton = document.createElement('button');
            userDeleteChangeButton.className = 'btn btn-danger btn-sm me-2';
            userDeleteChangeButton.title = '회원 탈퇴';
            userDeleteChangeButton.innerHTML = '<i class="ri-delete-bin-5-line"></i>';
            userDeleteChangeButton.addEventListener('click', function() {
                // 회원 탈퇴 로직
                // TODO : 회원 탈퇴 API 호출
            });

            if(user.user_delete_flag === true) {
                userDeleteChangeButton.innerHTML = '<i class="ri-key-line"></i>';
                userDeleteChangeButton.title = '회원 복구';
                userDeleteChangeButton.addEventListener('click', function () {
                    // 회원 복구 로직
                    // TODO : 회원 복구 API 호출
                });
            }

            userActionDiv.appendChild(userDeleteChangeButton);

            return userActionDiv;
        },
        drawPagination : function (currentPage, totalPage, pageSize) {
            $("#pagination-nav").twbsPagination('destroy');
            $("#pagination-nav").twbsPagination({
                first : null,
                last : null,
                prev : "<i class='ri-arrow-left-line'></i>",
                next : "<i class='ri-arrow-right-line'></i>",
                totalPages: totalPage,
                visiblePages: 5,
                startPage: currentPage,
                initiateStartPageClick: false,
                onPageClick: function (event, page) {
                    userinfo.offset = (page - 1) * pageSize;
                    userinfo.getUserList();
                }
            })
        }
    }
})();