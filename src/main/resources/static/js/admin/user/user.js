(function() {
    document.addEventListener('DOMContentLoaded', function() {
        userinfo.init();
    });

    let userinfo = {
        userRole: null,
        delete_flag: null,
        sortType: null,
        limit : 5,
        offset: 0,
        searhType: null,
        searchValue : null,
        init : function() {
            userinfo.getUserList();
            userinfo.filter();
            userinfo.search();
        },
        getUserList: function() {
            let url = '/admin/users/get';
            let params = {
                userRole: this.userRole,
                delete_flag: this.delete_flag,
                sortType: this.sortType,
                limit: this.limit,
                offset: this.offset,
                searchType: this.searchType,
                searchValue: this.searchValue
            };
            console.log(params);
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
                if(user.user_status === 1) {
                    userCard.className = 'user-card deleted-user';
                }

                let userCardContent = this.drawUserInfo(user);
                userCard.appendChild(userCardContent);

                userCardList.appendChild(userCard);

                userCardList.addEventListener('click', event => {
                    window.location.href = "/admin/users/detail/" + user.user_seq;
                })
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
            userRoleSpan.className = 'badge bg-secondary me-1';
            if(user.user_role === 'ADMIN') {
                userRoleSpan.className ='badge bg-primary me-1';
            }
            userRoleSpan.innerText = user.user_role.toLocaleLowerCase();

            let userisBotSpan = document.createElement('span');
            if(user.user_is_bot === 'Y') {
                userisBotSpan.className = 'badge bg-info me-1';
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
                userinfo.resetPwUser(user.user_seq);
            });
            userActionDiv.appendChild(userPwResetButton);

            let userRoleChangeButton = document.createElement('button');
            userRoleChangeButton.className = 'btn btn-primary btn-sm me-2';
            userRoleChangeButton.title = '권한 변경';
            userRoleChangeButton.innerHTML = '<i class="ri-shield-user-line"></i>';
            userRoleChangeButton.addEventListener('click', function() {
                userinfo.roleChange(user.user_seq);
            });
            userActionDiv.appendChild(userRoleChangeButton);

            let userDeleteChangeButton = document.createElement('button');
            userDeleteChangeButton.className = 'btn btn-danger btn-sm me-2';
            userDeleteChangeButton.title = '회원 탈퇴';
            userDeleteChangeButton.innerHTML = '<i class="ri-delete-bin-5-line"></i>';
            userDeleteChangeButton.addEventListener('click', function() {
                userinfo.deleteUser(user.user_seq);
            });

            if(user.user_status === 1) {
                userDeleteChangeButton.innerHTML = '<i class="ri-key-line"></i>';
                userDeleteChangeButton.title = '회원 복구';
                userDeleteChangeButton.addEventListener('click', function () {
                    userinfo.restoreUser(user.user_seq);
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
        },
        filter : function() {
            document.getElementById("filter-button").addEventListener('click', function () {
                let userRole = document.getElementById('user-role').value;
                let userStatus = document.getElementById('user-status').value;
                let sortType = document.getElementById('sort-type').value;


                let searchValue = document.getElementById('search-value').value;
                if(searchValue !== '') {
                    let searchType = document.getElementById('search-type').value;
                    userinfo.searchType = searchType === '' ? null : searchType;
                    userinfo.searchValue = searchValue === '' ? null : searchValue;
                }else{
                    userinfo.searchType = null;
                    userinfo.searchValue = null;
                }


                userinfo.userRole = userRole === '' ? null : userRole;
                userinfo.delete_flag = userStatus === '' ? null : (userStatus === 'active' ? 0 : 1);
                userinfo.sortType = sortType === 'id' ? null : sortType;
                userinfo.offset = 0;
                userinfo.getUserList();
            });
        },
        search : function() {
            document.getElementById("search-button").addEventListener('click', function () {
                let searchType = document.getElementById('search-type').value;
                let searchValue = document.getElementById('search-value').value;

                userinfo.searchType = searchType === '' ? null : searchType;
                userinfo.searchValue = searchValue === '' ? null : searchValue;
                userinfo.offset = 0;

                userinfo.getUserList();
            })
        },
        restoreUser : function(user_seq) {
            let url = '/admin/users/restore/'+ user_seq;
            fetch(url, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Network response was not ok'+ response.json().error_message);
                }
            }).then(data => {
                userinfo.getUserList();
            }).catch(error => {
                console.error('There was a problem with the fetch operation:', error);
            });
        },
        deleteUser : function(user_seq) {
            let url = '/admin/users/delete/'+ user_seq;
            fetch(url, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Network response was not ok'+ response.json().error_message);
                }
            }).then(data => {
                userinfo.getUserList();
            }).catch(error => {
                console.error('There was a problem with the fetch operation:', error);
            });
        },
        resetPwUser : function(user_seq) {
            let url = '/admin/users/resetpw/'+ user_seq;
            fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Network response was not ok'+ response.json().error_message);
                }
            }).then(data => {
                userinfo.getUserList();
            }).catch(error => {
                console.error('There was a problem with the fetch operation:', error);
            });
        },
        roleChange : function (user_seq) {
            let url = '/admin/users/roleChange/'+ user_seq;
            fetch(url, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Network response was not ok'+ response.json().error_message);
                }
            }).then(data => {
                userinfo.getUserList();
            }).catch(error => {
                console.error('There was a problem with the fetch operation:', error);
            });
        }
    }
})();