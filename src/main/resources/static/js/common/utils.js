const utils = {
    convertTime : function(timestamp) {
        const date = new Date(timestamp);
        const options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
        return date.toLocaleString('ko-KR', options).replace(/\s/, ' ');
    },
    // 시간 계산
    timeSince: function(date) {
        const seconds = Math.floor((new Date() - date) / 1000);
        let interval = Math.floor(seconds / 31536000);
        if (interval > 1) return interval + "년 전";
        interval = Math.floor(seconds / 2592000);
        if (interval > 1) return interval + "개월 전";
        interval = Math.floor(seconds / 86400);
        if (interval > 1) return interval + "일 전";
        interval = Math.floor(seconds / 3600);
        if (interval > 1) return interval + "시간 전";
        interval = Math.floor(seconds / 60);
        if (interval > 1) return interval + "분 전";
        return seconds + "초 전";
    }
}
