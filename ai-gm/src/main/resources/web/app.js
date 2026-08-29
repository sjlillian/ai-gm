const STORAGE_CAMPAIGN = "aigm.campaignId";
const STORAGE_CLIENT = "aigm.clientId";

const els = {
  campaignId: document.getElementById("campaign-id"),
  phase: document.getElementById("phase-badge"),
  clients: document.getElementById("clients"),
  idle: document.getElementById("idle-note"),
  promptStep: document.getElementById("prompt-step"),
  promptMessage: document.getElementById("prompt-message"),
  promptOptions: document.getElementById("prompt-options"),
  respondForm: document.getElementById("respond-form"),
  actions: document.getElementById("actions"),
  status: document.getElementById("status"),
  clientState: document.getElementById("client-state"),
  campaignState: document.getElementById("campaign-state"),
  situationPanel: document.getElementById("situation-panel"),
  situationText: document.getElementById("situation-text"),
  investigationText: document.getElementById("investigation-text"),
  opportunities: document.getElementById("opportunities"),
};

let view = null;
let selectedClientId = localStorage.getItem(STORAGE_CLIENT) || "campaign";
let busy = false;
let playKey = "";
let selectedChoice = "";
let friendId = "";
let rivalId = "";
let purveyorId = "";

document.getElementById("connect-form").addEventListener("submit", (event) => {
  event.preventDefault();
  connect("attach");
});
document.querySelectorAll(".connect [data-mode]").forEach((button) => {
  if (button.type === "submit") return;
  button.addEventListener("click", () => connect(button.dataset.mode));
});
els.respondForm.addEventListener("submit", (event) => {
  event.preventDefault();
  respond();
});

async function connect(mode) {
  const id = els.campaignId.value.trim() || "campaign-demo";
  els.campaignId.value = id;
  try {
    const body = mode === "attach"
      ? await api("/api/attach", { id })
      : await api("/api/start", { id, mode: mode === "demo" ? "demo" : "blank" });
    localStorage.setItem(STORAGE_CAMPAIGN, id);
    applyView(body.view);
    setStatus("Attached " + id, false);
  } catch (error) {
    setStatus(error.message, true);
  }
}

async function refresh() {
  if (busy) return;
  try {
    const next = await api("/api/view?client=" + encodeURIComponent(selectedClientId), null, "GET");
    applyView(next);
  } catch (error) {
    if (!view || !view.attached) {
      setStatus(error.message, true);
    }
  }
}

function applyView(next) {
  view = next;
  if (next.selected && next.selected.id) {
    selectedClientId = next.selected.id;
    localStorage.setItem(STORAGE_CLIENT, selectedClientId);
  }
  if (next.campaignId) {
    els.campaignId.value = next.campaignId;
    localStorage.setItem(STORAGE_CAMPAIGN, next.campaignId);
  }
  els.phase.textContent = next.attached ? (next.phase || "attached") : "idle";
  els.idle.classList.toggle("hidden", Boolean(next.attached));
  renderClients(next);
  renderSituation(next);
  const nextPlayKey = JSON.stringify({
    selected: next.selected && next.selected.id,
    prompt: next.selected && next.selected.prompt,
    respondable: next.respondable,
    actions: next.actions,
  });
  if (nextPlayKey !== playKey) {
    playKey = nextPlayKey;
    selectedChoice = "";
    friendId = "";
    rivalId = "";
    purveyorId = "";
    renderPrompt(next);
    renderActions(next);
  }
  els.clientState.replaceChildren(renderValue(next.selected ? next.selected.state : null));
  els.campaignState.replaceChildren(renderValue(summarizeCampaign(next)));
}

function renderClients(next) {
  els.clients.replaceChildren();
  (next.clients || []).forEach((client) => {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "client" + (client.id === selectedClientId ? " active" : "");
    button.innerHTML = `<span class="kind">${escapeHtml(client.kind)}</span>${escapeHtml(client.label)}`;
    button.addEventListener("click", () => {
      selectedClientId = client.id;
      localStorage.setItem(STORAGE_CLIENT, selectedClientId);
      refresh();
    });
    els.clients.appendChild(button);
  });
}

function renderSituation(next) {
  const snap = next.snapshot || {};
  const brief = snap.worldBrief || "";
  const jobs = snap.opportunities || [];
  const investigation = snap.lastInvestigation || "";
  const show = Boolean(brief || jobs.length || investigation);
  els.situationPanel.classList.toggle("hidden", !show);
  els.situationText.textContent = brief;
  els.investigationText.textContent = investigation;
  els.opportunities.replaceChildren();
  jobs.forEach((job) => {
    const card = document.createElement("div");
    card.className = "job";
    card.innerHTML =
      `<h3>${escapeHtml(job.title || job.id)}</h3>` +
      `<p class="meta">${escapeHtml([job.planType, job.district, job.targetName, job.targetTier].filter(Boolean).join(" · "))}</p>` +
      `<p>${escapeHtml(job.hook || "")}</p>`;
    els.opportunities.appendChild(card);
  });
}

function currentPrompt() {
  return view && view.selected && view.selected.prompt;
}

function readForm() {
  const values = {};
  Array.from(els.respondForm.elements || []).forEach((el) => {
    if (el.name) values[el.name] = el.value;
  });
  return values;
}

function renderPrompt(next, preserve) {
  const prompt = next.selected && next.selected.prompt;
  const saved = preserve ? readForm() : {};
  els.promptOptions.replaceChildren();
  els.respondForm.replaceChildren();
  if (!prompt) {
    els.promptStep.textContent = "";
    els.promptMessage.textContent = next.attached
      ? "This client has no current prompt."
      : "Nothing to answer yet.";
    els.respondForm.classList.add("hidden");
    return;
  }
  els.promptStep.textContent = prompt.step + (prompt.complete ? " · done" : "");
  els.promptMessage.textContent = prompt.message || "";
  const mode = prompt.selectionMode || (prompt.choices && prompt.choices.length ? "SINGLE" : "NONE");
  renderChoices(prompt, mode);
  if (!next.respondable) {
    els.respondForm.classList.add("hidden");
    return;
  }
  els.respondForm.classList.remove("hidden");
  if (mode === "PAIR") {
    const status = document.createElement("p");
    status.className = "pair-status";
    status.innerHTML =
      `<span>Friend: <strong>${escapeHtml(labelFor(prompt, friendId) || "—")}</strong></span>` +
      `<span>Rival: <strong>${escapeHtml(labelFor(prompt, rivalId) || "—")}</strong></span>`;
    els.respondForm.appendChild(status);
  }
  (prompt.fields || []).forEach((field) => {
    if (field.kind === "picks") {
      els.respondForm.appendChild(renderPicks(field, prompt));
      return;
    }
    const label = document.createElement("label");
    label.append(document.createTextNode(field.label || field.name));
    if (field.hint) {
      const hint = document.createElement("span");
      hint.className = "hint";
      hint.textContent = field.hint;
      label.appendChild(hint);
    }
    const input = field.kind === "textarea" ? document.createElement("textarea") : document.createElement("input");
    if (input.tagName === "INPUT") {
      input.type = (field.name === "friend" || field.name === "rival") ? "hidden" : "text";
    } else {
      input.rows = 3;
    }
    input.name = field.name;
    input.required = Boolean(field.required) && field.name !== "purveyorCustom" && field.name !== "friend" && field.name !== "rival";
    input.autocomplete = "off";
    if (saved[field.name]) input.value = saved[field.name];
    if (field.name === "friend") input.value = friendId;
    if (field.name === "rival") input.value = rivalId;
    if (field.name === "purveyor" && purveyorId) input.value = purveyorId;
    label.appendChild(input);
    els.respondForm.appendChild(label);
  });
  const submit = document.createElement("button");
  submit.type = "submit";
  submit.textContent = "Send response";
  els.respondForm.appendChild(submit);
}

function renderChoices(prompt, mode) {
  const choices = prompt.choices && prompt.choices.length
    ? prompt.choices
    : (prompt.options || []).map((id) => ({ id, label: id, description: "" }));
  if (!choices.length) {
    return;
  }
  const wrap = els.promptOptions;
  wrap.className = mode === "MAP" ? "options map-grid" : "options";
  choices.forEach((choice) => {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "choice-card" + selectedClass(choice.id, mode);
    button.innerHTML =
      `<span class="choice-title">${escapeHtml(choice.label || choice.id)}</span>` +
      (choice.description ? `<span class="choice-copy">${escapeHtml(choice.description)}</span>` : "");
    button.addEventListener("click", () => {
      if (mode === "PAIR") {
        if (!friendId || friendId === choice.id) {
          friendId = choice.id;
          if (rivalId === friendId) rivalId = "";
        } else {
          rivalId = choice.id;
        }
      } else {
        selectedChoice = choice.id;
      }
      renderPrompt(view, true);
    });
    wrap.appendChild(button);
  });
}

function selectedClass(id, mode) {
  if (mode === "PAIR") {
    if (id === friendId) return " selected friend";
    if (id === rivalId) return " selected rival";
    return "";
  }
  return id === selectedChoice ? " selected" : "";
}

function labelFor(prompt, id) {
  if (!id) return "";
  const found = (prompt.choices || []).find((choice) => choice.id === id);
  return found ? found.label : id;
}

function renderPicks(field, prompt) {
  const wrap = document.createElement("div");
  const heading = document.createElement("p");
  heading.className = "kicker";
  heading.textContent = field.label || "Pick one";
  wrap.appendChild(heading);
  if (field.hint) {
    const hint = document.createElement("p");
    hint.className = "hint";
    hint.textContent = field.hint;
    wrap.appendChild(hint);
  }
  const list = document.createElement("div");
  list.className = "options";
  const group = selectedChoice;
  (field.options || [])
    .filter((option) => !group || !option.group || option.group === group)
    .forEach((option) => {
      const button = document.createElement("button");
      button.type = "button";
      button.className = "choice-card" + (purveyorId === option.id ? " selected" : "");
      button.innerHTML =
        `<span class="choice-title">${escapeHtml(option.label || option.id)}</span>` +
        (option.description ? `<span class="choice-copy">${escapeHtml(option.description)}</span>` : "");
      button.addEventListener("click", () => {
        purveyorId = option.id;
        renderPrompt(view, true);
      });
      list.appendChild(button);
    });
  wrap.appendChild(list);
  const hidden = document.createElement("input");
  hidden.type = "hidden";
  hidden.name = field.name;
  hidden.value = purveyorId;
  wrap.appendChild(hidden);
  return wrap;
}

function renderActions(next) {
  els.actions.replaceChildren();
  (next.actions || []).forEach((action) => {
    const form = document.createElement("form");
    form.className = "action-form";
    const title = document.createElement("h3");
    title.textContent = action.label;
    form.appendChild(title);
    (action.fields || []).forEach((field) => {
      form.appendChild(renderField(field, next));
    });
    const submit = document.createElement("button");
    submit.type = "submit";
    submit.textContent = action.label;
    form.appendChild(submit);
    form.addEventListener("submit", async (event) => {
      event.preventDefault();
      const fields = {};
      (action.fields || []).forEach((field) => {
        const input = form.elements[field.name];
        if (!input) return;
        fields[field.name] = field.kind === "checkbox" ? input.checked : input.value;
      });
      try {
        const body = await api("/api/action", {
          id: action.id,
          clientId: selectedClientId,
          fields,
        });
        applyView(body.view);
        setStatus(formatResult(body.result), false);
      } catch (error) {
        setStatus(error.message, true);
      }
    });
    els.actions.appendChild(form);
  });
}

function renderField(field, next) {
  const label = document.createElement("label");
  if (field.kind === "checkbox") {
    label.className = "check";
    const input = document.createElement("input");
    input.type = "checkbox";
    input.name = field.name;
    label.append(input, document.createTextNode(field.label));
    return label;
  }
  label.append(document.createTextNode(field.label));
  let input;
  if (field.kind === "select") {
    input = document.createElement("select");
    const jobs = (next.snapshot && next.snapshot.opportunities) || [];
    (field.options || []).forEach((option) => {
      const item = document.createElement("option");
      item.value = option;
      const job = jobs.find((entry) => entry.id === option);
      item.textContent = job ? job.title : option;
      input.appendChild(item);
    });
  } else {
    input = document.createElement("input");
    input.type = field.kind === "number" ? "number" : "text";
  }
  input.name = field.name;
  input.required = Boolean(field.required);
  input.autocomplete = "off";
  label.appendChild(input);
  return label;
}

async function respond() {
  const prompt = currentPrompt();
  const fields = {};
  if (els.respondForm.elements) {
    Array.from(els.respondForm.elements).forEach((el) => {
      if (!el.name) return;
      fields[el.name] = el.value;
    });
  }
  const token = selectedChoice || fields.token || fields.name || "";
  if (friendId) fields.friend = friendId;
  if (rivalId) fields.rival = rivalId;
  if (purveyorId && !fields.purveyorCustom) fields.purveyor = purveyorId;
  if (fields.purveyorCustom) fields.purveyor = fields.purveyorCustom;
  try {
    const body = await api("/api/respond", {
      clientId: selectedClientId,
      token,
      rest: fields.detail || fields.look || "",
      fields,
    });
    const joined = prompt && prompt.step === "WAITING_FOR_JOIN" && (fields.name || token);
    applyView(body.view);
    if (joined) {
      selectedClientId = (fields.name || token).trim();
      localStorage.setItem(STORAGE_CLIENT, selectedClientId);
      await refresh();
    }
    setStatus("Response sent.", false);
  } catch (error) {
    setStatus(error.message, true);
  }
}

function summarizeCampaign(next) {
  if (!next || !next.snapshot) return null;
  const snap = next.snapshot;
  return {
    campaignWorkflowId: snap.campaignWorkflowId,
    phase: snap.phase,
    cycleNumber: snap.cycleNumber,
    worldBrief: snap.worldBrief,
    opportunities: snap.opportunities,
    lastInvestigation: snap.lastInvestigation,
    downtimeEntanglement: snap.downtimeEntanglement,
    crew: snap.crew,
    crewClocks: snap.crewClocks,
    pcWorkflowIds: snap.pcWorkflowIds,
    activeScoreWorkflowId: snap.activeScoreWorkflowId,
    activeDowntimeWorkflowId: snap.activeDowntimeWorkflowId,
    engagementPosition: snap.engagementPosition,
    scoreClocks: snap.scoreClocks,
    lastAdjudication: snap.lastAdjudication,
    downtimeChoices: snap.downtimeChoices,
    sessionZero: snap.sessionZero,
    creationPrompt: snap.creationPrompt,
  };
}

function renderValue(value) {
  const root = document.createElement("div");
  root.appendChild(nodeFor("value", value, true));
  return root;
}

function nodeFor(key, value, open) {
  if (isClock(value)) {
    const row = document.createElement("div");
    row.className = "clock";
    const label = document.createElement("span");
    label.innerHTML = `<span class="key">${escapeHtml(value.name || key)}</span>`;
    const bar = document.createElement("div");
    bar.className = "clock-bar";
    const fill = document.createElement("span");
    fill.style.width = Math.round((value.progress / Math.max(value.max, 1)) * 100) + "%";
    bar.appendChild(fill);
    const nums = document.createElement("span");
    nums.textContent = value.progress + "/" + value.max;
    row.append(label, bar, nums);
    return row;
  }
  if (value === null || value === undefined) {
    return leaf(key, "null");
  }
  if (typeof value !== "object") {
    return leaf(key, String(value));
  }
  const details = document.createElement("details");
  details.open = Boolean(open);
  const summary = document.createElement("summary");
  summary.innerHTML = `<span class="key">${escapeHtml(key)}</span>`;
  details.appendChild(summary);
  if (Array.isArray(value)) {
    if (value.length === 0) {
      details.appendChild(leaf("(empty)", "[]"));
    } else {
      value.forEach((item, index) => details.appendChild(nodeFor(String(index), item, false)));
    }
    return details;
  }
  const keys = Object.keys(value);
  if (keys.length === 0) {
    details.appendChild(leaf("(empty)", "{}"));
  } else {
    keys.forEach((child) => details.appendChild(nodeFor(child, value[child], false)));
  }
  return details;
}

function leaf(key, text) {
  const row = document.createElement("div");
  row.innerHTML = `<span class="key">${escapeHtml(key)}</span>: <span class="val">${escapeHtml(text)}</span>`;
  return row;
}

function isClock(value) {
  return Boolean(
    value &&
      typeof value === "object" &&
      !Array.isArray(value) &&
      typeof value.progress === "number" &&
      typeof value.max === "number"
  );
}

async function api(path, body, method) {
  busy = true;
  try {
    const response = await fetch(path, {
      method: method || (body ? "POST" : "GET"),
      headers: body ? { "Content-Type": "application/json" } : undefined,
      body: body ? JSON.stringify(body) : undefined,
    });
    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
      throw new Error(data.error || response.statusText);
    }
    return data;
  } finally {
    busy = false;
  }
}

function setStatus(text, isError) {
  els.status.hidden = !text;
  els.status.textContent = text || "";
  els.status.className = "status " + (isError ? "error" : "ok");
}

function formatResult(result) {
  if (result == null) return "ok";
  if (typeof result === "string") return result;
  return JSON.stringify(result);
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;");
}

const saved = localStorage.getItem(STORAGE_CAMPAIGN);
if (saved) {
  els.campaignId.value = saved;
}
refresh();
setInterval(refresh, 2000);
