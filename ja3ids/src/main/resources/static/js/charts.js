let threatChart = null;
let hitsThreatChart = null;
let chartsInitialized = false;

/* ================================================= */
/* INIT CHARTS (SAFE + ONE TIME) */
/* ================================================= */
function initCharts() {
    if (chartsInitialized) return;

    const threatCanvas = document.getElementById("threatChart");
    const hitsCanvas = document.getElementById("hitsThreatChart");

    if (!threatCanvas || !hitsCanvas) {
        console.error("❌ Chart canvas not found");
        return;
    }

    chartsInitialized = true;
    console.log("✅ Initializing charts");

    // =========================
    // Threat Level Over Time
    // =========================
    threatChart = new Chart(threatCanvas.getContext("2d"), {
        type: "line",
        data: {
            labels: [],
            datasets: [{
                label: "Threat Level",
                data: [],
                borderColor: "#ef4444",
                backgroundColor: "rgba(239,68,68,0.25)",
                borderWidth: 2,
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: false,
            scales: {
                y: {
                    min: 0,
                    max: 10,
                    ticks: { color: "#94a3b8" },
                    grid: { color: "rgba(148,163,184,0.1)" }
                },
                x: {
                    ticks: { color: "#94a3b8" },
                    grid: { display: false }
                }
            },
            plugins: {
                legend: {
                    labels: { color: "#e5e7eb" }
                }
            }
        }
    });

    // =========================
    // Hits vs Threat Level
    // =========================
    hitsThreatChart = new Chart(hitsCanvas.getContext("2d"), {
        type: "line",
        data: {
            labels: [],
            datasets: [
                {
                    label: "Hits",
                    data: [],
                    borderColor: "#38bdf8",
                    backgroundColor: "rgba(56,189,248,0.25)",
                    borderWidth: 2,
                    tension: 0.4,
                    fill: true
                },
                {
                    label: "Threat Level",
                    data: [],
                    borderColor: "#f59e0b",
                    backgroundColor: "rgba(245,158,11,0.25)",
                    borderWidth: 2,
                    tension: 0.4,
                    fill: true
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: false,
            scales: {
                y: {
                    min: 0,
                    max: 25,
                    ticks: { color: "#94a3b8" },
                    grid: { color: "rgba(148,163,184,0.1)" }
                },
                x: {
                    ticks: { color: "#94a3b8" },
                    grid: { display: false }
                }
            },
            plugins: {
                legend: {
                    labels: { color: "#e5e7eb" }
                }
            }
        }
    });
}

/* ================================================= */
/* UPDATE CHARTS (SAFE) */
/* ================================================= */
async function updateCharts() {
    if (!threatChart || !hitsThreatChart) return;

    try {
        const res = await fetch("/api/ja3/logs");
        const logs = await res.json();

        if (!Array.isArray(logs) || logs.length === 0) return;

        const recent = logs.slice(0, 20).reverse();

        // Clear existing data
        threatChart.data.labels.length = 0;
        threatChart.data.datasets[0].data.length = 0;

        hitsThreatChart.data.labels.length = 0;
        hitsThreatChart.data.datasets[0].data.length = 0;
        hitsThreatChart.data.datasets[1].data.length = 0;

        recent.forEach(e => {
            if (!e.timestamp) return;

            const time = new Date(e.timestamp).toLocaleTimeString();

            threatChart.data.labels.push(time);
            threatChart.data.datasets[0].data.push(e.threatLevel || 0);

            hitsThreatChart.data.labels.push(time);
            hitsThreatChart.data.datasets[0].data.push(e.hits || 0);
            hitsThreatChart.data.datasets[1].data.push(e.threatLevel || 0);
        });

        threatChart.update();
        hitsThreatChart.update();

    } catch (err) {
        console.error("❌ Chart update failed", err);
    }
}

/* ================================================= */
/* SAFE BOOTSTRAP */
/* ================================================= */
document.addEventListener("DOMContentLoaded", () => {
    setTimeout(() => {
        initCharts();
        updateCharts();
        setInterval(updateCharts, 2000);
    }, 300); // allow layout + CSS to settle
});
