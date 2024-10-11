var player;
var seekBar, currentTimeDisplay, durationDisplay;
var videoIdToLoad;

function onYouTubeIframeAPIReady() {
    if (videoIdToLoad) {
        loadVideo(videoIdToLoad);
    }
}

function loadVideo(videoId) {
    if (typeof YT === 'undefined' || !YT.loaded) {
        videoIdToLoad = videoId;
        return;
    }

    // Initialize DOM element references
    seekBar = document.getElementById("seek-bar");
    currentTimeDisplay = document.getElementById("current-time");
    durationDisplay = document.getElementById("duration");

    player = new YT.Player('player', {
        width: '620',
        height: '320',
        videoId: videoId,
        playerVars: {
            'autoplay': 0,
            'controls': 0,
        },
        events: {
            'onReady': onPlayerReady,
            'onStateChange': onPlayerStateChange
        }
    });

    // Add event listener for seek bar
    if (seekBar) {
        seekBar.addEventListener("change", function () {
            var time = player.getDuration() * (seekBar.value / 100);
            player.seekTo(time);
        });
    }
}

function onPlayerReady(event) {
    event.target.setPlaybackQuality('small');
    updateTimerDisplay();
    updateProgressBar();
}

function onPlayerStateChange(event) {
    if (event.data == YT.PlayerState.PLAYING) {
        setTimeout(updateTimerDisplay, 1000);
        setTimeout(updateProgressBar, 1000);
    }
}

function formatTime(time) {
    time = Math.round(time);
    var minutes = Math.floor(time / 60);
    var seconds = time - minutes * 60;
    seconds = seconds < 10 ? '0' + seconds : seconds;
    return minutes + ":" + seconds;
}

function updateTimerDisplay() {
    if (currentTimeDisplay && durationDisplay && player) {
        currentTimeDisplay.innerHTML = formatTime(player.getCurrentTime());
        durationDisplay.innerHTML = formatTime(player.getDuration());

        if (player.getPlayerState() == 1) {
            setTimeout(updateTimerDisplay, 1000);
        }
    }
}

function updateProgressBar() {
    if (seekBar && player) {
        seekBar.value = (player.getCurrentTime() / player.getDuration()) * 100;

        if (player.getPlayerState() == 1) {
            setTimeout(updateProgressBar, 1000);
        }
    }
}