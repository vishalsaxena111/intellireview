const codeInput = document.querySelector("#codeInput");
const analyzeButton = document.querySelector("#analyzeButton");
const sampleButton = document.querySelector("#sampleButton");
const statusBox = document.querySelector("#status");

const sampleCode = `import java.util.HashMap;
import java.util.Map;

public class TwoSum {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int needed = target - nums[i];
            if (seen.containsKey(needed)) {
                return new int[] { seen.get(needed), i };
            }
            seen.put(nums[i], i);
        }

        return new int[] {};
    }
}`;

codeInput.value = sampleCode;

sampleButton.addEventListener("click", () => {
    codeInput.value = sampleCode;
    codeInput.focus();
});

analyzeButton.addEventListener("click", async () => {
    const codeText = codeInput.value.trim();

    if (!codeText) {
        showError("Paste Java code before running analysis.");
        return;
    }

    setBusy(true);
    showStatus("Analyzing code...");

    try {
        const response = await fetch("/api/v1/code-review/analyze", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                language: "java",
                codeText
            })
        });

        const payload = await response.json();

        if (!response.ok) {
            showError(payload.error || "Analysis failed.");
            return;
        }

        renderResult(payload);
    } catch (error) {
        showError("Could not reach the analyzer service.");
    } finally {
        setBusy(false);
    }
});

function renderResult(result) {
    statusBox.classList.toggle("error", !result.parsedSuccessfully);
    statusBox.textContent = result.parsedSuccessfully
        ? (result.summary || "Analysis complete.")
        : (result.parseMessage || result.summary || "The code could not be parsed.");

    document.querySelector("#timeComplexity").textContent = result.timeComplexity || "-";
    document.querySelector("#spaceComplexity").textContent = result.spaceComplexity || "-";
    document.querySelector("#loopCount").textContent = result.structure?.totalLoopCount ?? "-";
    document.querySelector("#methodCount").textContent = result.structure?.methodCount ?? "-";

    renderList("#patterns", result.dsaPatterns);
    const aiReviewCompleted = renderAiReview(result.aiReview);
    document.querySelectorAll(".static-review-section, #patterns")
        .forEach(section => section.closest(".result-section")?.classList.toggle("hidden", aiReviewCompleted));
    renderList("#optimizations", result.optimizationSuggestions);
    renderList("#correctness", result.correctnessWarnings);
    renderList("#smells", result.codeSmells);
    renderList("#suggestions", result.suggestions);
}

function renderAiReview(aiReview) {
    const status = document.querySelector("#aiStatus");
    const summary = document.querySelector("#aiSummary");
    const improvedCode = document.querySelector("#aiImprovedCode");

    status.textContent = aiReview?.status || "AI review unavailable.";
    summary.textContent = aiReview?.summary || "";

    renderList("#aiCorrectness", aiReview?.correctnessIssues);
    renderList("#aiComplexity", aiReview?.complexityNotes);
    renderList("#aiPatterns", aiReview?.dsaPatterns);
    renderList("#aiOptimization", aiReview?.optimizationPlan);

    if (aiReview?.improvedCode) {
        improvedCode.style.display = "block";
        improvedCode.textContent = aiReview.improvedCode;
    } else {
        improvedCode.style.display = "none";
        improvedCode.textContent = "";
    }

    return Boolean(aiReview?.enabled && aiReview?.summary);
}

function renderList(selector, items) {
    const list = document.querySelector(selector);
    list.replaceChildren();

    if (!items || items.length === 0) {
        const item = document.createElement("li");
        item.textContent = "None detected.";
        list.appendChild(item);
        return;
    }

    for (const value of items) {
        const item = document.createElement("li");
        item.textContent = value;
        list.appendChild(item);
    }
}

function showStatus(message) {
    statusBox.classList.remove("error");
    statusBox.textContent = message;
}

function showError(message) {
    statusBox.classList.add("error");
    statusBox.textContent = message;
}

function setBusy(isBusy) {
    analyzeButton.disabled = isBusy;
    analyzeButton.textContent = isBusy ? "Analyzing" : "Analyze";
}
