// DOM Elements
const searchBtn = document.getElementById("searchBtn");
const sendBtn = document.getElementById("sendBtn");
const artistInput = document.getElementById("artistInput");
const chatInput = document.getElementById("chatInput");
const artistInfoDiv = document.getElementById("artistInfo");
const chatOutputDiv = document.getElementById("chatOutput");
const quizContainer = document.getElementById("quizContainer");
const submitQuizBtn = document.getElementById("submitQuizBtn");
const quizResult = document.getElementById("quizResult");

let quizData = [];

// Helper function to fetch JSON safely
async function fetchJSON(url) {
    const res = await fetch(url);
    if (!res.ok) throw new Error(`Network error: ${res.status}`);
    return res.json();
}

// Helper to append chat messages
function appendMessage(type, text) {
    const msgDiv = document.createElement("div");
    msgDiv.className = `message ${type}`;
    msgDiv.innerHTML = text;
    chatOutputDiv.appendChild(msgDiv);
    chatOutputDiv.scrollTop = chatOutputDiv.scrollHeight;
}

// Handle artist search
searchBtn.addEventListener("click", async () => {
    const artist = artistInput.value.trim();
    if (!artist) return;

    artistInfoDiv.textContent = "Loading artist info...";
    artistInfoDiv.style.color = "black";
    chatOutputDiv.innerHTML = "";
    quizContainer.innerHTML = "";
    quizResult.textContent = "";
    submitQuizBtn.style.display = "none";

    try {
        const data = await fetch(`/api/chatmusic?artist=${encodeURIComponent(artist)}`)
            .then(res => res.text());

        artistInfoDiv.textContent = data || "No information found for this artist.";

        // Remove old quiz button if exists
        const oldQuizBtn = document.querySelector(".quiz-btn");
        if (oldQuizBtn) oldQuizBtn.remove();

        // Create new quiz button
        const quizBtn = document.createElement("button");
        quizBtn.textContent = `Generate Quiz about ${artist}`;
        quizBtn.classList.add("quiz-btn");
        artistInfoDiv.insertAdjacentElement("afterend", quizBtn);

        // Quiz button click
        quizBtn.addEventListener("click", async () => {
            quizContainer.innerHTML = "<p>Generating quiz...</p>";
            quizResult.textContent = "";
            submitQuizBtn.style.display = "none";

            try {
                const data = await fetch(`/api/music/quiz?artist=${encodeURIComponent(artist)}`)
                    .then(res => res.json()); // safe now
                quizData = data;

                quizContainer.innerHTML = quizData.map((q, i) => `
                    <div class="quiz-question">
                        <p><strong>Q${i + 1}:</strong> ${q.question}</p>
                        ${q.options.map(opt => `
                            <label class="option">
                                <input type="radio" name="q${i}" value="${opt[0]}"> ${opt}
                            </label>
                        `).join("")}
                    </div>
                `).join("");
                submitQuizBtn.style.display = "block";

            } catch (err) {
                quizContainer.innerHTML = "<p style='color:red;'>Error generating quiz.</p>";
                console.error(err);
            }
        });

    } catch (err) {
        artistInfoDiv.textContent = "Error fetching artist info.";
        artistInfoDiv.style.color = "red";
        console.error(err);
    }
});

// Handle chat send
async function sendMessage() {
    const message = chatInput.value.trim();
    if (!message) return;

    appendMessage("user-msg", message);
    chatInput.value = "";

    const typingMessage = document.createElement("div");
    typingMessage.className = "message ai-msg";
    typingMessage.textContent = "AI is typing...";
    chatOutputDiv.appendChild(typingMessage);
    chatOutputDiv.scrollTop = chatOutputDiv.scrollHeight;

    try {
        const data = await fetch(`/api/chat?message=${encodeURIComponent(message)}`)
            .then(res => res.text());

        typingMessage.remove();
        appendMessage("ai-msg", data);

    } catch (err) {
        typingMessage.remove();
        appendMessage("error-msg", "Error getting response.");
        console.error(err);
    }
}

sendBtn.addEventListener("click", sendMessage);

// Support Enter key for chat input
chatInput.addEventListener("keypress", (e) => {
    if (e.key === "Enter") sendMessage();
});

// Submit quiz
submitQuizBtn.addEventListener("click", () => {
    let score = 0;

    quizData.forEach((q, i) => {
        const selected = document.querySelector(`input[name="q${i}"]:checked`);
        if (selected && selected.value === q.correct) score++;
    });

    quizResult.textContent = `You got ${score} out of ${quizData.length} correct!`;
});
