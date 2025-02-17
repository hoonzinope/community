(function() {
    $(document).ready(function() {
       console.log('Hello World');
        getPosts();
    });

    function getPosts() {
        fetch('/api/posts')
            .then(function(response) {
                response.json().then(function(data) {
                    drawPostTable(data);
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
        console.log(post);
        if(post.category === "General"){
            category =  `<span class="badge bg-info">${post.category}</span>`
        }else if(post.category === "Discussion"){
            category =  `<span class="badge bg-success">${post.category}</span>`
        }else if(post.category === "Question"){
            category =  `<span class="badge bg-warning">${post.category}</span>`
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
})();