const input = document.getElementById("searchInput");
const button = document.getElementById("searchButton");
const result = document.getElementById("result");

button.addEventListener("click", () => {
    search(input.value);
});

input.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        search(input.value);
    }
});

async function search(query) {
    if (!query) {
        return;
    }

    result.innerHTML = '<p class="meta">Searching...</p>';

    const [videosResponse, channelsResponse] = await Promise.all([
        fetch(`/api/videos/search/${encodeURIComponent(query)}`),
        fetch(`/api/channels/search/${encodeURIComponent(query)}`)
    ]);

    if (!videosResponse.ok && !channelsResponse.ok) {
        result.innerHTML = '<p class="meta error">Search is currently unavailable.</p>';
        return;
    }

    const videos = videosResponse.ok ? await videosResponse.json() : [];
    const channels = channelsResponse.ok ? await channelsResponse.json() : [];

    renderResults(videos, channels);
}

function renderResults(videos, channels) {
    if (videos.length === 0 && channels.length === 0) {
        result.innerHTML = `<p class="meta">No results found.</p>`;
        return;
    }

    result.innerHTML = `
        ${channels.length ? `
            <h3 class="section-title">Channels</h3>
            <div class="video-grid">
                ${channels.map(renderChannelTile).join("")}
            </div>
        ` : ""}

        ${videos.length ? `
            <h3 class="section-title">Videos</h3>
            <div class="video-grid">
                ${videos.map(renderVideoTile).join("")}
            </div>
        ` : ""}
    `;
}