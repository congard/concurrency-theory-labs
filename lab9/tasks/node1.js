function printAsync(s, callback) {
    var delay = Math.floor((Math.random() * 1000) + 500);

    setTimeout(() => {
        console.log(s);

        if (callback) {
            callback();
        }
    }, delay);
}

printAsync("1");
printAsync("2");
printAsync("3");

console.log('done!');
