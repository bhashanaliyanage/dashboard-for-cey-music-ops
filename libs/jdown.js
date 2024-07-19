const { spawn } = require('child_process');
const path = require('path');
const fs = require('fs');

const findExecutable = () => {
    const possibleExecutables = ['yt-dlp', 'yt-dlp.exe', 'youtube-dl', 'youtube-dl.exe'];
    const customPath = path.join(__dirname, 'node_modules', 'youtube-dl-exec', 'bin');

    for (const exe of possibleExecutables) {
        if (fs.existsSync(path.join(customPath, exe))) {
            return path.join(customPath, exe);
        }
    }

    // If not found in the custom path, return just the executable name
    // and rely on it being in the system PATH
    return possibleExecutables[0];
};

const runCommand = (command, args) => {
    return new Promise((resolve, reject) => {
        const process = spawn(command, args, { stdio: 'inherit' });

        process.on('close', (code) => {
            if (code === 0) {
                resolve();
            } else {
                reject(new Error(`Process exited with code ${code}`));
            }
        });

        process.on('error', (err) => {
            reject(err);
        });
    });
};

const downloadVideo = async (videoURL, outputPath) => {
    const executable = findExecutable();
    console.log(`Using executable: ${executable}`);

    const args = [
        videoURL,
        '-f', 'bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best',
        '-o', outputPath,
        '--no-check-certificate',
        '--no-warnings',
        '--prefer-free-formats',
        '--add-header', 'referer:youtube.com',
        '--add-header', 'user-agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    ];

    try {
        console.log('Starting download...');
        await runCommand(executable, args);
        console.log('Download complete!');
    } catch (error) {
        console.error('Download failed:', error.message);
        throw error;
    }
};

const main = async () => {
    const args = process.argv.slice(2);
    const videoURL = args[0];
    const outputPath = args[1];

    if (!videoURL || !outputPath) {
        console.error('Usage: node script.js <YouTube URL> <output file path>');
        process.exit(1);
    }

    try {
        await downloadVideo(videoURL, outputPath);
    } catch (error) {
        console.error('Script execution failed:', error.message);
        process.exit(1);
    }
};

main();