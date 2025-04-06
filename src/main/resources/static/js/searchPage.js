(function() {
    document.addEventListener('DOMContentLoaded', function() {
        searchPage.init();
    });

    const searchPage = {
        init: function() {
            console.log('data loaded');
            console.log(data);
            searchPage.search();
            searchPage.render(data);
        },
        search : function() {
            const searchButton = document.getElementById("searchBtn");

            searchButton.addEventListener('click', function() {
                // 클릭 시점에 최신 값을 가져옴
                let searchType = document.getElementById('searchType').value;
                let searchInput = document.getElementById('searchKeyword').value;
                searchInput = encodeURIComponent(searchInput);

                const queryString = "?keyword=" + searchInput
                    + "&searchType=" + searchType
                    + "&offset=0&limit=10";

                console.log("clicked "+ queryString);
                if (searchInput) {
                    window.location.href = '/search' + queryString;
                }
            });

            // Enter 키로도 검색할 수 있게 추가
            document.getElementById('searchKeyword').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    document.getElementById('searchBtn').click();
                }
            });
        },
        render : function(data) {
            // search
            searchPage.renderSearchBar(data);

            if(data.post_list.length === 0) {
                let noData = document.getElementById('noData');
                noData.innerHTML = "검색 결과가 없습니다.";
                noData.style.display = "block";
                document.getElementById('tableHead').style.display = "none";
                document.getElementById('pagination').style.display = "none";
            }else{
                // table
                //document.getElementById('tableHead').style.display = "";
                let tbody = document.getElementById('rows');
                let rows = searchPage.renderRows(data);
                tbody.innerHTML = rows;

                // pagination
                searchPage.renderPagination(data);
            }
        },
        renderSearchBar : function(data) {
            let searchResult = document.getElementById('search_result');
            searchResult.innerHTML = data.keyword+' 검색결과';

            let searchType = document.getElementById('searchType');
            let len = searchType.options.length;
            // select box의 option 갯수만큼 for문 돌림
            for (let i = 0; i < len; i++){
                // select box의 option value가 입력 받은 값과 일치할 경우 selected
                if (searchType.options[i].value === data.type){
                    searchType.options[i].selected = true;
                    break;
                }
            }

            let searchKeyword = document.getElementById('searchKeyword');
            searchKeyword.value = data.keyword;
        },
        renderRows : function(data) {
            let postList = data.postList;
            let rows = '';
            postList.forEach(post => {
                post.insert_ts = searchPage.convertTime(post.insert_ts);
                rows += `
                    <tr>
                        <td><span class="badge bg-info">${post.category}</span></td>
                        <td><a href="/post/${post.post_seq}">${post.title}</a></td>
                        <td>${post.view_count}</td>
                        <td>${post.insert_ts}</td>
                        <td>
                            <i class="ri-thumb-up-line text-primary"></i> ${post.like_count}
                            <i class="ri-thumb-down-line text-secondary ms-2"></i> ${post.dislike_count}
                        </td>
                    </tr>
                `;
            });
            return rows;
        },
        renderPagination : function(data) {
            let total = data.total;
            let limit = data.limit;
            let totalPages = Math.ceil(total / limit);
            let offset = data.offset;
            let currentPage = Math.floor(offset / limit) + 1;
            console.log("currentPage: " + currentPage);

            $("#pagination").twbsPagination({
                first : null,
                last : null,
                totalPages: totalPages,
                visiblePages: 5,
                startPage: currentPage,
                initiateStartPageClick: false,
                onPageClick: function (event, page) {
                    let searchType = document.getElementById('searchType').value;
                    let searchInput = document.getElementById('searchKeyword').value;
                    searchInput = encodeURIComponent(searchInput);

                    const queryString = "?keyword=" + searchInput
                        + "&searchType=" + searchType
                        + "&offset=" + (page - 1) * limit
                        + "&limit=" + limit;

                    console.log("clicked "+ queryString);
                    window.location.href = '/search' + queryString;
                }
            });
        },
        convertTime : function(timestamp) {
            return utils.convertTime(timestamp);
        }
    }
})();