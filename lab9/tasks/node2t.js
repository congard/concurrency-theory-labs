const { countSync, countAsync } = require("./node2.js")

const DATASET_DIR = "PAM08"

/**
 * 
 * @param {async () => void} func 
 * @param {number} n 
 * @returns {Promise<number>}
 */
async function benchmark(func, n) {
    let time = 0

    for (let i = 0; i < n; ++i) {
        const startTime = Date.now()
        await func()
        time += Date.now() - startTime
    }

    return time / n;
}

async function runBenchmark() {
    console.log("Starting syncMeanTime benchmark")
    const syncMeanTime = await benchmark(() => countSync(DATASET_DIR), 100)
    console.log("syncMeanTime", syncMeanTime)

    console.log("Starting asyncMeanTime benchmark")
    const asyncMeanTime = await benchmark(() => countAsync(DATASET_DIR), 100)
    console.log("asyncMeanTime", asyncMeanTime)
}

async function runDemo() {
    let syncLineCount = 0
    let asyncLineCount = 0

    console.log("Sync line counter")

    await countSync("PAM08", (filename, count) => {
        console.log(filename, count)
        syncLineCount += count
    })

    console.log("Async line counter")

    await countAsync("PAM08", (filename, count) => {
        console.log(filename, count)
        asyncLineCount += count
    })

    console.assert(syncLineCount == asyncLineCount)

    console.log("Total line count:", syncLineCount)
}

// runDemo()
runBenchmark()
