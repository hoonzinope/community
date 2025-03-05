(function() {
    $(document).ready(function() {
        getPosts(0, 5);
    });

    function getPosts(offset, limit) {
        let endpoint = "/api/posts?offset=" + offset+ "&limit=" + limit;
        fetch(endpoint)
            .then(function(response) {
                response.json().then(function(data) {
                    drawPostTable(data);
                    drawPagination(data, offset, limit);
                });
            })
            .catch(function(err) {
                console.log(err);
            });
    }

    function drawPostTable(data) {
        let posts = data.postList;
        let rows = posts.map(function(post) {
            return drawRow(post);
        });

        $("#rows").html(`${rows.join('')}`);
    }

    function drawRow(post) {
        let category = '';
        if(post.category === "General"){
            category =  `<span class="badge bg-info">${post.category}</span>`
        }else if(post.category === "Discussion"){
            category =  `<span class="badge bg-success">${post.category}</span>`
        }else if(post.category === "Question"){
            category =  `<span class="badge bg-warning">${post.category}</span>`
        }else{
            category =  `<span class="badge bg-info">${post.category}</span>`
        }


        return `<tr>
            <td>${category}</td>
            <td><a href="/post/${post.post_seq}" class="text-decoration-none text-dark">${post.title}</a></td>
            <td><i class="ri-eye-line"></i> ${post.view_count}</td>
            <td>${post.insert_ts}</td>
            <td>
                <i class="ri-thumb-up-line text-primary"></i> ${post.like_count} / 
                <i class="ri-thumb-down-line text-secondary ms-2"></i> ${post.dislike_count}
            </td>
        </tr>`;
    }

    function drawPagination(data, offset, limit) {
        let total = data.total;
        let totalPages = Math.ceil(total / limit);
        let currentPage = offset + 1;

        $("#pagination").twbsPagination({
            first : null,
            last : null,
            initiateStartPageClick : false,
            totalPages : totalPages,
            visiblePages : 5,
            onPageClick : function(event, page) {
                getPosts((page-1) * limit, 5);
            }
        })
    }
})();