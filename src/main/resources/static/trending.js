const regionInput = document.getElementById("regionInput");
const categoryInput = document.getElementById("categoryInput");
const button = document.getElementById("searchButton");
const result = document.getElementById("result");

button.addEventListener("click", () => {
    fetchTrending(regionInput.value, categoryInput.value);
});

async function fetchTrending(regionCode, categoryId) {
    if (!regionCode) {
        result.innerHTML = '<p class="meta error">Enter a region code, e.g. US.</p>';
        return;
    }

    result.innerHTML = '<p class="meta">Loading...</p>';

    const url = categoryId
        ? `/api/videos/trending?region=${encodeURIComponent(regionCode)}&category=${encodeURIComponent(categoryId)}`
        : `/api/videos/trending?region=${encodeURIComponent(regionCode)}`;

    const response = await fetch(url);

    if (!response.ok) {
        const message = await response.text();
        result.innerHTML = `<p class="meta error">${message}</p>`;
        return;
    }

    const videos = await response.json();

    if (videos.length === 0) {
        result.innerHTML = '<p class="meta">No trending videos found for that region/category.</p>';
        return;
    }

    result.innerHTML = `
        <div class="video-grid">
            ${videos.map(renderVideoTile).join("")}
        </div>
    `;
}
