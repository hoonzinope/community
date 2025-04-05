const utils = {
    convertTime : function(timestamp) {
        const date = new Date(timestamp);
        const options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
        return date.toLocaleString('ko-KR', options).replace(/\s/, ' ');
    }
}
