function renderLineChart(dataPoints, valueOf, unitLabel) {
    if (!dataPoints || dataPoints.length < 2) {
        return `<p class="meta">Not enough history yet to show a chart.</p>`;
    }

    const values = dataPoints.map(valueOf);
    const minValue = Math.min(...values);
    const maxValue = Math.max(...values);
    const range = maxValue - minValue || 1;

    const width = 560;
    const height = 160;
    const padding = 20;

    const points = dataPoints.map((point, index) => {
        const x = padding + (index / (dataPoints.length - 1)) * (width - padding * 2);
        const y = height - padding - ((valueOf(point) - minValue) / range) * (height - padding * 2);
        return `${x.toFixed(1)},${y.toFixed(1)}`;
    }).join(" ");

    return `
        <svg viewBox="0 0 ${width} ${height}" class="growth-chart">
            <polyline points="${points}" fill="none" stroke="#3b5bfd" stroke-width="2" />
        </svg>
        <p class="meta">${minValue.toLocaleString()} &ndash; ${maxValue.toLocaleString()} ${unitLabel} across ${dataPoints.length} snapshots</p>
    `;
}

function renderVideoTile(video) {
    const thumbnailUrl = video.thumbnails[0].url;
    return `
        <a class="video-tile" href="video.html?videoInput=${video.videoId}">
            <img src="${thumbnailUrl}">
            <p>${video.videoTitle}</p>
            <p class="meta">${video.viewCount.toLocaleString()} views</p>
        </a>
    `;
}

function renderChannelTile(channel) {
    const thumbnailUrl = channel.thumbnails[0].url;
    return `
        <a class="video-tile" href="channel.html?channelInput=${channel.ytId}">
            <img src="${thumbnailUrl}">
            <p>${channel.channelName}</p>
            <p class="meta">${channel.subscribers.toLocaleString()} subscribers</p>
        </a>
    `;
}
