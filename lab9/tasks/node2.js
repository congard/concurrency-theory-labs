const fs = require("fs");

module.exports = {
    countSync: countSync,
    countAsync: countAsync
}

/**
 * @param {string} path 
 * @param {(path: string, count: number) => void | undefined} callback 
 * @returns {Promise<void>}
 */
async function countSync(path, callback) {
    await forEachFile(path, async file => {
        const lines = await countLines(file);

        if (callback) {
            callback(file, lines)
        }
    })
}

/**
 * @param {string} path 
 * @param {(path: string, count: number) => void | undefined} callback
 * @returns {Promise<void>} 
 */
async function countAsync(path, callback) {
    return new Promise(async resolve => {
        let counter = 0
        let treeBuilt = false

        await forEachFile(path, file => {
            ++counter

            countLines(file).then(val => {
                --counter

                if (callback) {
                    callback(file, val)
                }

                if (counter == 0 && treeBuilt) {
                    resolve()
                }
            })
        })

        treeBuilt = true
    })
}

async function forEachFile(dir, callback) {
    for (let file of fs.readdirSync(dir)) {
        file = dir + "/" + file

        if (fs.statSync(file).isDirectory()) {
            await forEachFile(file, callback)
        } else {
            await callback(file)
        }
    }
}

async function countLines(filename) {
    return new Promise((resolve, reject) => {
        let count = 0

        fs.createReadStream(filename).on('data', function(chunk) {
            count += chunk.toString('utf8')
                .split(/\r\n|[\n\r\u0085\u2028\u2029]/g)
                .length - 1;
        }).on('end', function() {
            resolve(count)
        }).on('error', function (err) {
            reject(err)
        });
    })
}
