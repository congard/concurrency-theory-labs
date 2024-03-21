function printAsync(s, callback) {
    var delay = Math.floor((Math.random() * 1000) + 500);

    setTimeout(() => {
        console.log(s);

        if (callback) {
            callback();
        }
    }, delay);
}

function loop(n) {
    if (n == 0) {
        console.log('done!');
        return;
    }

    printAsync(1, () => {
        printAsync(2, () => {
            printAsync(3, () => {
                loop(n - 1);
            });
        });
    });
}

loop(3);
