const fs = require('fs');
const ytdl = require('ytdl-core');

const downloadAudio = async (videoURL, filePath) => {
    try {
        if (ytdl.validateURL(videoURL)) {
            console.log("URL:", videoURL);
            const videoInfo = await ytdl.getInfo(videoURL);
            const audioFormats = ytdl.filterFormats(videoInfo.formats, 'audioonly');
            const audioReadableStream = ytdl.downloadFromInfo(videoInfo, { format: audioFormats[0] });
            audioReadableStream.pipe(fs.createWriteStream(filePath));
            console.log('Audio downloading...');
        } else {
            console.error('Invalid YouTube video URL');
        }
    } catch (error) {
        console.error('Error:', error.message);
    }
};

const args = process.argv.slice(2);
const videoURL = args[0];
const filePath = args[1];
downloadAudio(videoURL, filePath);

// const fs = require('fs');
// const ytdl = require('ytdl-core');
// const ffmpeg = require('fluent-ffmpeg');

// const downloadAndConvertAudio = async (videoURL, filePath) => {
//     try {
//         if (ytdl.validateURL(videoURL)) {
//             console.log("URL:", videoURL);
//             const videoInfo = await ytdl.getInfo(videoURL);
//             const audioFormats = ytdl.filterFormats(videoInfo.formats, 'audioonly');
//             const audioReadableStream = ytdl.downloadFromInfo(videoInfo, { format: audioFormats[0] });
//             const flacFilePath = filePath.replace(/\.[^/.]+$/, ".flac"); // Replace file extension with .flac

//             audioReadableStream.pipe(fs.createWriteStream(filePath));
//             console.log('Audio downloading...');

// // Convert downloaded audio to FLAC format
// ffmpeg()
//     .input(audioReadableStream)
//     .audioCodec('flac')
//     .audioBitrate(24)
//     .audioFrequency(44100)
//     .output(flacFilePath)
//     .on('end', () => {
//         console.log('Audio converted to FLAC successfully.');
//     })
//     .on('error', (err) => {
//         console.error('Error converting audio to FLAC:', err);
//     })
//     .run();
//         } else {
//             console.error('Invalid YouTube video URL');
//         }
//     } catch (error) {
//         console.error('Error:', error.message);
//     }
// };

// const args = process.argv.slice(2);
// const videoURL = args[0];
// const filePath = args[1];
// downloadAndConvertAudio(videoURL, filePath);

