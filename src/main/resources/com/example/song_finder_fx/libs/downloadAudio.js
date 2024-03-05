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
