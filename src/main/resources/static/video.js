const input = document.getElementById("videoInput");
const button = document.getElementById("searchButton");
const result = document.getElementById("result");

button.addEventListener("click", () => {
    fetchVideo(input.value);
});

const preloadedVideo = new URLSearchParams(window.location.search).get("videoInput");
if (preloadedVideo) {
    input.value = preloadedVideo;
    fetchVideo(preloadedVideo);
}

function extractVideoId(rawInput) {
    const cleaned = rawInput.trim();
    if (!cleaned.includes("/")) {
        return cleaned;
    }
    let idPart;
    if (cleaned.includes("watch?v=")) {
        idPart = cleaned.substring(cleaned.indexOf("watch?v=") + 8);
    } else {
        idPart = cleaned.substring(cleaned.lastIndexOf("/") + 1);
    }
    return idPart.split(/[?&#]/)[0];
}

async function fetchVideo(videoInput) {
    const videoId = extractVideoId(videoInput);
    result.innerHTML = '<p class="meta">Loading...</p>';

    const [videoResponse, insightsResponse, growthResponse, commentsResponse] = await Promise.all([
        fetch(`/api/videos/${encodeURIComponent(videoId)}`),
        fetch(`/api/videos/${encodeURIComponent(videoId)}/video_insights`),
        fetch(`/api/videos/${encodeURIComponent(videoId)}/growth`),
        fetch(`/api/comments/${encodeURIComponent(videoId)}/all`)
    ]);

    if (!videoResponse.ok) {
        const message = await videoResponse.text();
        result.innerHTML = `<p class="meta error">${message}</p>`;
        return;
    }

    const video = await videoResponse.json();
    const insights = insightsResponse.ok ? await insightsResponse.json() : null;
    const growth = growthResponse.ok ? await growthResponse.json() : [];
    const comments = commentsResponse.ok ? await commentsResponse.json() : [];

    renderVideo(video, insights, growth, comments);
}

function formatDate(isoString) {
    if (!isoString) {
        return "unknown date";
    }
    return new Date(isoString).toLocaleDateString();
}

function formatDuration(totalSeconds) {
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${minutes}:${String(seconds).padStart(2, "0")}`;
}

function formatPercent(ratio) {
    return `${(ratio * 100).toFixed(2)}%`;
}

function renderVideo(video, insights, growth, comments) {
    const thumbnailUrl = video.thumbnails[0].url;

    result.innerHTML = `
        <div class="video-card">
            <img src="${thumbnailUrl}" width="320">
            <h2>${video.videoTitle}</h2>
            <p class="channel-name">${video.channelTitle}</p>
            <p class="meta">Published ${formatDate(video.publishedAt)} &middot; Duration ${formatDuration(video.durationInSeconds)}</p>
            <p class="stats">
                ${video.viewCount.toLocaleString()} views &middot;
                ${video.likeCount.toLocaleString()} likes (${formatPercent(video.likeToViewRatio)}) &middot;
                ${video.commentCount.toLocaleString()} comments (${formatPercent(video.commentToViewRatio)})
            </p>
            <p class="description">${video.videoDescription ?? ""}</p>
            ${video.tags && video.tags.length ? `<p class="meta">Tags: ${video.tags.join(", ")}</p>` : ""}
        </div>

        ${renderInsights(insights)}

        <div class="growth-card">
            <h3>Growth</h3>
            ${renderLineChart([...growth].reverse(), s => s.viewCount, "views")}
        </div>

        <div class="milestone-card">
            <h3>Milestone Calculator</h3>
            <div class="search-bar">
                <input type="number" id="milestoneTarget" class="text-input" placeholder="Target views">
                <button id="milestoneButton" class="button">Calculate</button>
            </div>
            <p id="milestoneResult" class="meta"></p>
        </div>

        <div class="comments-card">
            <h3>Comments</h3>
            ${renderComments(comments)}
        </div>
    `;

    document.getElementById("milestoneButton").addEventListener("click", () => {
        calculateMilestone(video.videoId);
    });
}

function renderComments(comments) {
    if (!comments || comments.length === 0) {
        return `<p class="meta">No comments found.</p>`;
    }

    return comments.map(comment => `
        <div class="comment">
            <p class="comment-author">${comment.authorName}</p>
            <p class="comment-text">${comment.text}</p>
            <p class="meta">${comment.likeCount.toLocaleString()} likes &middot; ${formatDate(comment.publishedAt)}</p>
        </div>
    `).join("");
}

function renderInsights(insights) {
    if (!insights) {
        return "";
    }

    const momentumLabel = insights.momentum > 0 ? "accelerating" : insights.momentum < 0 ? "slowing down" : "steady";
    const badgeClass = insights.outperformingChannel ? "badge positive" : "badge negative";
    const badgeText = insights.outperformingChannel ? "Outperforming channel average" : "Below channel average";

    return `
        <div class="insights-card">
            <h3>Insights</h3>
            <p>${insights.viewIncreaseFromLastSnapshot.toLocaleString()} views since last check</p>
            <p>Average velocity: ${insights.averageViewsVelocity.toFixed(1)} views/day,
               ${insights.averageLikesVelocity.toFixed(1)} likes/day,
               ${insights.averageCommentVelocity.toFixed(1)} comments/day</p>
            <p>Momentum: ${momentumLabel} (${insights.momentum.toFixed(1)} views/day change)</p>
            <span class="${badgeClass}">${badgeText}</span>
        </div>
    `;
}

async function calculateMilestone(videoId) {
    const milestoneResult = document.getElementById("milestoneResult");
    const target = document.getElementById("milestoneTarget").value;

    if (!target) {
        milestoneResult.textContent = "Enter a target view count.";
        return;
    }

    milestoneResult.textContent = "Calculating...";

    const response = await fetch(`/api/videos/${videoId}/days_to_milestone?target=${encodeURIComponent(target)}`);

    if (!response.ok) {
        milestoneResult.textContent = await response.text();
        return;
    }

    const days = await response.json();

    if (days === 0) {
        milestoneResult.textContent = "This video has already reached that milestone.";
    } else {
        milestoneResult.textContent = `At the current rate, about ${Math.ceil(days)} days to reach ${Number(target).toLocaleString()} views.`;
    }
}