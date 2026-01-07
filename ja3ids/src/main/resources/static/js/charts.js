let threatChart;
let hitsThreatChart;

function initCharts() {

    // =========================
    // Threat Level Over Time
    // =========================
    const threatCtx = document.getElementById("threatChart").getContext("2d");

    threatChart = new Chart(threatCtx, {
        type: "line",
        data: {
            labels: [],
            datasets: [{
                label: "Threat Level",
                data: [],
                borderWidth: 2,
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    min: 0,
                    max: 10
                }
            }
        }
    });

    // =========================
    // Hits vs Threat Level
    // =========================
    const hitsThreatCtx = document
        .getElementById("hitsThreatChart")
        .getContext("2d");

    hitsThreatChart = new Chart(hitsThreatCtx, {
        type: "line",
        data: {
            labels: [],
            datasets: [
                {
                    label: "Hits",
                    data: [],
                    borderWidth: 2,
                    tension: 0.4
                },
                {
                    label: "Threat Level",
                    data: [],
                    borderWidth: 2,
                    tension: 0.4
                }
            ]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    min: 0,
                    max: 25
                }
            }
        }
    });
}

async function updateCharts() {
    const res = await fetch("/api/ja3/logs");
    const logs = await res.json();
    if (!Array.isArray(logs)) return;

    const recent = logs.slice(0, 20).reverse();

    // Reset data
    threatChart.data.labels = [];
    threatChart.data.datasets[0].data = [];

    hitsThreatChart.data.labels = [];
    hitsThreatChart.data.datasets[0].data = [];
    hitsThreatChart.data.datasets[1].data = [];

    recent.forEach(e => {
        const time = new Date(e.timestamp).toLocaleTimeString();

        // Threat chart
        threatChart.data.labels.push(time);
        threatChart.data.datasets[0].data.push(e.threatLevel);

        // Hits vs Threat chart
        hitsThreatChart.data.labels.push(time);
        hitsThreatChart.data.datasets[0].data.push(e.hits);
        hitsThreatChart.data.datasets[1].data.push(e.threatLevel);
    });

    threatChart.update();
    hitsThreatChart.update();
}

// Init charts after page load
window.addEventListener("load", () => {
    initCharts();
    updateCharts();
    setInterval(updateCharts, 2000);
});
