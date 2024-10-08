const ffmpeg = require('fluent-ffmpeg');

async function cutMP3(inputFile, outputFile, startTime, endTime) {
    const duration = calculateDuration(startTime, endTime);
    const fadeInDuration = 1; // Specify the duration of the fade-in effect (in seconds)
    const fadeOutDuration = 1; // Specify the duration of the fade-out effect (in seconds)
    console.log(duration);
    return new Promise((resolve, reject) => {
        ffmpeg(inputFile)
            .setStartTime(startTime)
            .duration(duration)
            .audioFilters([
                `afade=t=in:ss=0:d=${fadeInDuration}`, // Fade-in effect
                `afade=t=out:st=${duration - fadeOutDuration}:d=${fadeOutDuration}`, // Fade-out effect
            ])
            .output(outputFile)
            .on('end', () => {
                console.log(`Output file saved as ${outputFile}`);
                resolve();
            })
            .on('error', (err) => {
                console.error('Error:', err.message);
                reject(err);
            })
            .run();
    });
}

// Function to calculate duration based on start and end times
function calculateDuration(startTime, endTime) {
    const start = parseTime(startTime);
    const end = parseTime(endTime);
    return end - start;
}

// Function to parse time strings to seconds
function parseTime(timeStr) {
    const [hours, minutes, seconds] = timeStr.split(':').map(Number);
    return hours * 3600 + minutes * 60 + seconds;
}

// Example usage
const [inputFile, outputFile, startTime, endTime] = process.argv.slice(2);
console.log(startTime);
console.log(endTime);
cutMP3(inputFile, outputFile, startTime, endTime);
