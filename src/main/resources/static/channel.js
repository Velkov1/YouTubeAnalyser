const input = document.getElementById("channelInput");
const button = document.getElementById("searchButton");
const result = document.getElementById("result");

button.addEventListener("click", () => {
    fetchChannel(input.value);
});

const preloadedChannel = new URLSearchParams(window.location.search).get("channelInput");
if (preloadedChannel) {
    input.value = preloadedChannel;
    fetchChannel(preloadedChannel);
}

function extractChannelInput(rawInput) {
    const cleaned = rawInput.trim();
    if (!cleaned.includes("/")) {
        return cleaned;
    }
    if (cleaned.includes("watch?v=") || cleaned.includes("youtu.be/") || cleaned.includes("/shorts/")) {
        return null;
    }
    const withoutQuery = cleaned.split(/[?&#]/)[0];
    const segments = withoutQuery.split("/").filter(Boolean);
    return segments[segments.length - 1];
}

async function fetchChannel(channelInput) {
    const channelId = extractChannelInput(channelInput);
    if (!channelId) {
        result.innerHTML = '<p class="meta error">That looks like a video link, not a channel link. Try the Video page instead.</p>';
        return;
    }
    result.innerHTML = '<p class="meta">Loading...</p>';

    const [insightsResponse, videosResponse, historyResponse, risingResponse] = await Promise.all([
        fetch(`/api/videos/${encodeURIComponent(channelId)}/insights`),
        fetch(`/api/videos/${encodeURIComponent(channelId)}/most_watched/limit=12`),
        fetch(`/api/channels/${encodeURIComponent(channelId)}/history`),
        fetch(`/api/videos/${encodeURIComponent(channelId)}/rising_videos/limit=12`)
    ]);

    if (!insightsResponse.ok) {
        const message = await insightsResponse.text();
        result.innerHTML = `<p class="meta error">${message}</p>`;
        return;
    }

    const insights = await insightsResponse.json();
    const videos = videosResponse.ok ? await videosResponse.json() : [];
    const history = historyResponse.ok ? await historyResponse.json() : [];
    const rising = risingResponse.ok ? await risingResponse.json() : [];

    renderChannel(insights, videos, history, rising);
}

function formatChannelAge(months) {
    const years = Math.floor(months / 12);
    const remainingMonths = months % 12;
    if (years === 0) {
        return `${remainingMonths} months`;
    }
    return `${years}y ${remainingMonths}m`;
}

function capitalize(word) {
    return word.charAt(0) + word.slice(1).toLowerCase();
}

function mostCommon(map) {
    const entries = Object.entries(map);
    if (entries.length === 0) {
        return null;
    }
    return entries.reduce((best, current) => (current[1] > best[1] ? current : best));
}

function renderChannel(insights, videos, history, rising) {
    const channel = insights.channel;
    const thumbnailUrl = channel.thumbnails[0].url;

    const busiestDay = mostCommon(insights.uploadDayDistribution);
    const busiestHour = mostCommon(insights.uploadHourDistribution);

    result.innerHTML = `
        <div class="video-card">
            <img src="${thumbnailUrl}" width="160">
            <h2>${channel.channelName}</h2>
            <p class="meta">${channel.country ?? "Unknown country"} &middot; Channel age ${formatChannelAge(channel.channelAgeInMonths)}</p>
            <p class="stats">
                ${channel.subscribers.toLocaleString()} subscribers &middot;
                ${channel.totalViews.toLocaleString()} views &middot;
                ${channel.numVideos.toLocaleString()} videos
            </p>
            <p class="description">${channel.description ?? ""}</p>
        </div>

        <div class="insights-card">
            <h3>Channel Insights</h3>
            <p>${channel.averageViewsPerVideo.toFixed(0)} average views per video</p>
            <p>${channel.viewsPerSubscriber.toFixed(1)} views per subscriber</p>
            <p>${channel.uploadsPerMonth.toFixed(1)} uploads per month</p>
            <p>${insights.averageVideoLengthMinutes.toFixed(1)} minute average video length</p>
            <p>${(insights.viewConcentration * 100).toFixed(1)}% of views come from the top 10% of videos</p>
            ${busiestDay ? `<p>Uploads most often on ${capitalize(busiestDay[0])}</p>` : ""}
            ${busiestHour ? `<p>Most common upload hour: ${busiestHour[0]}:00</p>` : ""}
        </div>

        <div class="growth-card">
            <h3>Subscriber Growth</h3>
            ${renderLineChart([...history].reverse(), s => s.subscribers, "subscribers")}
        </div>

        <h3 class="section-title">Top Videos</h3>
        <div class="video-grid">
            ${videos.map(renderVideoTile).join("")}
        </div>

        ${rising.length ? `
            <h3 class="section-title">Rising Videos</h3>
            <div class="video-grid">
                ${rising.map(renderVideoTile).join("")}
            </div>
        ` : ""}
    `;
}