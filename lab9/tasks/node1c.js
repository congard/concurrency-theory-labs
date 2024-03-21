function printAsync(s, callback) {
    var delay = Math.floor((Math.random() * 1000) + 500);

    setTimeout(() => {
        console.log(s);

        if (callback) {
            callback();
        }
    }, delay);
}

function task(n) {
    return new Promise((resolve, reject) => {
        printAsync(n, () => {
            resolve(n);
        });
    });
}


// 'then' returns a new Promise, therefore we can chain another 'then'.
// In this case 'task(x)' directly returns a Promise object, however
// 'then' could also return a value in which case it would be wrapped
// in a Promise that would be automatically resolved with that value.

/*
** Zadanie:
** Napisz funkcje loop(m), ktora powoduje wykonanie powyzszej
** sekwencji zadan m razy.
**
*/

function loop(m) {
    if (m == 0)
        return;

    task(1).then((n) => {
        console.log('task', n, 'done');
        return task(2);
    }).then((n) => {
        console.log('task', n, 'done');
        return task(3);
    }).then((n) => {
        console.log('task', n, 'done');
        console.log('done!');
    }).then(() => {
        loop(m - 1);
    });
}

loop(4);
