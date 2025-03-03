(function() {
    console.log("post.js");
    document.addEventListener("DOMContentLoaded", function() {
        console.log(likeData);
        getLikeType();
        like();
        dislike();
        deletePost();
    });

    function getLikeType() {
        if(likeData.user_seq == null || likeData.user_seq === "") {
            return;
        }

        fetch("/like/get", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(likeData)})
            .then(function(response) {
                response.json().then(function(data) {
                    console.log(data);
                    if(data.like_type == "LIKE") {
                        $("#like").addClass("active");
                    }
                    else if(data.like_type == "DISLIKE") {
                        $("#dislike").addClass("active");
                    }
                });
            }).catch(function(err) {
            console.log(err);
        });
    }

    function like() {
        $("#like").off("click").on("click", function() {
            if(likeData.user_seq == null || likeData.user_seq == "") {
                alert("로그인이 필요한 서비스입니다.");
                return;
            }
            let button = $("#like");
            let like_cnt = $("#like_cnt").text().replace("(", "").replace(")", "");
            let dislike_cnt = $("#dislike_cnt").text().replace("(", "").replace(")", "");
            if(button.hasClass("active")) {
                button.removeClass("active");

                deleteLikeType("LIKE");
                like_cnt = parseInt(like_cnt) - 1;
                $("#like_cnt").text("(" + like_cnt + ")");
            }
            else {
                if($("#dislike").hasClass("active")) {
                    $("#dislike").removeClass("active");
                    deleteLikeType("DISLIKE");
                    dislike_cnt = parseInt(dislike_cnt) - 1;
                    $("#dislike_cnt").text("(" + dislike_cnt + ")");
                }

                button.addClass("active");
                addLikeType("LIKE");
                like_cnt = parseInt(like_cnt) + 1;
                $("#like_cnt").text("(" + like_cnt + ")");
            }
        });
    }

    function dislike() {
        $("#dislike").off("click").on("click", function() {
            if(likeData.user_seq == null || likeData.user_seq == "") {
                alert("로그인이 필요한 서비스입니다.");
                return;
            }
            let button = $("#dislike");
            let like_cnt = $("#like_cnt").text().replace("(", "").replace(")", "");
            let dislike_cnt = $("#dislike_cnt").text().replace("(", "").replace(")", "");
            if(button.hasClass("active")) {
                button.removeClass("active");

                deleteLikeType("DISLIKE");
                dislike_cnt = parseInt(dislike_cnt) - 1;
                $("#dislike_cnt").text("(" + dislike_cnt + ")");
            }
            else {
                if ($("#like").hasClass("active")) {
                    $("#like").removeClass("active");
                    deleteLikeType("LIKE");
                    like_cnt = parseInt(like_cnt) - 1;
                    $("#like_cnt").text("(" + like_cnt + ")");
                }

                button.addClass("active");
                addLikeType("DISLIKE");
                dislike_cnt = parseInt(dislike_cnt) + 1;
                $("#dislike_cnt").text("(" + dislike_cnt + ")");
            }
        });
    }

    function deleteLikeType(like_type) {
        likeData.like_type = like_type;

        fetch("/api/like/delete", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(likeData)})
            .then(function(response) {
                response.json().then(function(data) {
                    console.log(data);
                });
            }).catch(function(err) {
            console.log(err);
        });
    }

    function addLikeType(like_type){
        likeData.like_type = like_type;

        fetch("/api/like/add", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(likeData)})
            .then(function(response) {
                response.json().then(function(data) {
                    console.log(data);
                });
            }).catch(function(err) {
            console.log(err);
            });
    }

    function deletePost() {
        $("#delete_post").off('click').on("click", function() {
           let check = confirm("delete post?");
           if(!check) return;
           let endpoint = `/api/post/${likeData.post_seq}`;
           fetch(endpoint, {
               method : "DELETE"
           }).then(function(response) {
               response.json().then(function(data) {
                   console.log(data);
                   location.href = '/board';
               });
           }).catch(function(err) {
               console.log(err);
           });
        });
    }

})();