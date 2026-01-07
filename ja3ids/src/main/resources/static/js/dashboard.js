let lastRenderedTimestamp = null;

async function loadDashboard() {
    try {
        /* =========================
           1️⃣ FETCH LATEST (CARD)
        ========================= */
        const latestRes = await fetch("/api/ja3/latest");
        const latest = await latestRes.json();

        if (latest && latest.ja3Hash) {
            document.getElementById("ja3").textContent = latest.ja3Hash;
            document.getElementById("hits").textContent = latest.hits;
            document.getElementById("threat").textContent = latest.threatLevel;
            document.getElementById("status").textContent = latest.status;
            document.getElementById("message").textContent = latest.message;

            document.getElementById("status").className =
                "value " + latest.status;
        }

        /* =========================
           2️⃣ FETCH LOGS (FEED)
        ========================= */
        const logsRes = await fetch("/api/ja3/logs");
        const logs = await logsRes.json();

        if (!Array.isArray(logs)) return;

        const logFeed = document.getElementById("logFeed");
        if (!logFeed) return;

        // Clear only once on first load
        if (lastRenderedTimestamp === null) {
            logFeed.innerHTML = "";
        }

        // Logs are newest-first from backend → reverse for terminal order
        logs.reverse().forEach(event => {
            if (!event.timestamp) return;

            if (lastRenderedTimestamp &&
                event.timestamp <= lastRenderedTimestamp) {
                return;
            }

            let cls = "log-ok";
            if (event.status === "WARNING") cls = "log-warn";
            if (event.malicious === true) cls = "log-bad";

            const time = new Date(event.timestamp).toLocaleTimeString();

            const line = document.createElement("div");
            line.className = `log-line ${cls}`;
            line.textContent =
                `[${time}] IDS_KERNEL: [IDS RESULT] ` +
                JSON.stringify(event);

            logFeed.appendChild(line);
        });

        // Update last timestamp
        if (logs.length > 0) {
            lastRenderedTimestamp = logs[0].timestamp;
        }

        // Auto-scroll
        logFeed.scrollTop = logFeed.scrollHeight;

    } catch (err) {
        console.error("Dashboard error:", err);
    }
}

// ⏱ Poll every 2 seconds
setInterval(loadDashboard, 2000);
loadDashboard();
