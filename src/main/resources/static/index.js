const socketUrl = `ws://${window.location.host}/messages`;
const ws = new WebSocket(socketUrl);

// ws.onopen = function () {
//     ws.send(JSON.stringify(msg));
// };

ws.onmessage = function (message) {
    let state = JSON.parse(message.data);
    document.getElementById("log").innerText = state.join(",");
};

ws.onclose = function () {
    console.log("ws closed.");
};

ws.onerror = function (err) {
    console.log("ws err:", err);
};

document.querySelector("button").addEventListener("click", () => {
    const input = document.querySelector("input");
    const payload = {
        message: input.value
    };
    const config = {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    };
    fetch("/api/message", config);
    input.value = "";
});