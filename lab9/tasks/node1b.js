const async = require('async');

/**
 * @param {any} s 
 * @param {() => void} callback 
 */
function printAsync(s, callback) {
    let delay = Math.floor((Math.random() * 1000) + 500);

    setTimeout(() => {
        console.log(s);

        if (callback) {
            callback();
        }
    }, delay);
}

function execute(tasks) {

    function iterate(index) {
        // tasks are finished
        if (index === tasks.length) {
            return;
        }

        // set the current task
        const task = tasks[index];

        /* executes the current task passing the 'iterate' function as a callback, it will be called by the task itself */
        task(() => iterate(index + 1));
    }

    return iterate(0);
}

function task1(callback) {
    printAsync("1", callback);
}

function task2(callback) {
    printAsync("2", callback);
}

function task3(callback) {
    printAsync("3", callback);
}

/**
 * @param {any} s 
 * @param {(e: Error, [results]) => void} callback 
 */
function callAsync(s, callback) {
    var delay = Math.floor((Math.random() * 1000) + 500);

    setTimeout(() => {
        console.log("Running", s)

        if (callback) {
            callback(null, s);
        }
    }, delay);
}

async function main() {
    console.log("waterfall naive")

    await async.waterfall([
        task1,
        task2,
        task3
    ]).then((val) => {
        console.log(val);
    })

    console.log("waterfall")

    // https://caolan.github.io/async/v3/docs.html#waterfall

    await async.waterfall([
        callback => {
            callAsync(1, callback);
        },
        (prev, callback) => {
            console.log("Task", prev, "done");
            callAsync(2, callback)
        },
        (prev, callback) => {
            console.log("Task", prev, "done");
            callAsync(3, callback);
        }
    ]).then(val => {
        console.log("Task", val, "done");
        console.log("Done");
    })
}

// execute([task1, task2, task3]);

main()