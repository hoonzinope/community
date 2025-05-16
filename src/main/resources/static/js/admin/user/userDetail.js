(function() {
   document.addEventListener("DOMContentLoaded", function() {
       let location_info = location.href.split("/");
       let userSeq = location_info[location_info.length - 1];
       userInfo.init(userSeq);
   });

   const userInfo = {
       user_seq : null,
       init: function(userSeq) {
           this.user_seq = userSeq;
           this.getUserInfo();
       },
       getUserInfo: function() {
           // TODO : 서버에서 사용자 정보 가져오기
       }
   }
});