(function() {
    document.addEventListener('DOMContentLoaded', function() {
        searchPage.init();
    });

    const searchPage = {
        init: function() {
            console.log('data loaded');
            console.log(data);
            console.log(data.post_list);
            console.log(data.search_result);

            searchPage.search();
            searchPage.renderTbody(data);
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
        renderTbody : function(data) {
            let tbody = document.getElementById('rows');
            let rows = searchPage.renderRows(data.post_list);
            tbody.innerHTML = rows;
        },
        renderRows : function(data) {
            let postList = data.postList;
            let rows = '';
            postList.forEach(post => {
                rows += `
                    <tr>
                        <td><span class="badge bg-info">${post.category}</span></td>
                        <td><a href="/post/${post.post_seq}">${post.title}</a></td>
                        <td>${post.view_count}</td>
                        <td>${post.insert_ts}</td>
                        <td>
                            <i class="ri-thumb-up-line text-primary"></i> 24
                            <i class="ri-thumb-down-line text-secondary ms-2"></i> 3
                        </td>
                    </tr>
                `;
            });
            return rows;
        }
    }
})();