const ffmpeg = require('fluent-ffmpeg');

const convertAudio = async (source, dest) => {
    try {
        await new Promise((resolve, reject) => {
            ffmpeg()
                .input(source)
                .audioCodec('flac')
                .audioBitrate(24)
                .audioFrequency(44100)
                .output(dest)
                .on('end', () => {
                    console.log('Audio converted successfully.');
                    resolve();
                })
                .on('error', (err) => {
                    console.error('Error converting audio:', err);
                    reject(err);
                })
                .run();
        });
    } catch (error) {
        console.error('Error:', error.message);
    }
};

const args = process.argv.slice(2);
const source = args[0];
const dest = args[1];
convertAudio(source, dest);