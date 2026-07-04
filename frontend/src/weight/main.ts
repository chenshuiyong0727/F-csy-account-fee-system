import * as echarts from "echarts";
import "./style.css";

interface WeightRecord {
  date: string;
  weight: number;
  fat: number;
  fatEstimated: boolean;
}

interface WeightData {
  nickname: string;
  avatar?: string;
  heightCm: number;
  startDate: string;
  endDate: string;
  startWeight: number;
  targetWeight: number;
  todayWeight: number;
  changeFromLast: number;
  totalLoss: number;
  distanceTarget: number;
  bmi: number;
  fat: number;
  fatEstimated: boolean;
  days: number;
  progress: number;
  records: WeightRecord[];
}

interface ApiResult<T> { code: number; msg: string; data: T }
let chart: echarts.ECharts | undefined;
const profileId = new URLSearchParams(window.location.search).get("id");

const loading = element("loading");
const error = element("error");
const content = element("content");
element("retryButton").addEventListener("click", load);
window.addEventListener("resize", () => chart?.resize());

load();

async function load() {
  showState("loading");
  try {
    const apiUrl = profileId ? `/api/public/weight?id=${encodeURIComponent(profileId)}` : "/api/public/weight";
    const response = await fetch(apiUrl, {
      headers: { Accept: "application/json" },
      cache: "no-store"
    });
    if (!response.ok) throw new Error("网络请求失败");
    const result = await response.json() as ApiResult<WeightData>;
    if (result.code !== 200 || !result.data) throw new Error(result.msg || "分享地址无效");
    render(result.data);
    showState("content");
    requestAnimationFrame(() => chart?.resize());
  } catch (reason) {
    showError(reason instanceof Error ? reason.message : "请稍后重试");
  }
}

function render(data: WeightData) {
  setText("nickname", data.nickname);
  const avatar = element("avatar") as HTMLImageElement;
  avatar.src = data.avatar || "/brand/person.png";
  avatar.onerror = () => { avatar.src = "/brand/person.png"; };
  setText("days", data.days);
  setText("todayWeight", fixed(data.todayWeight));
  setText("fatRate", `${fixed(data.fat)}%`);
  element("fatEstimated").classList.toggle("hidden", !data.fatEstimated);
  setText("bmi", fixed(data.bmi));
  setText("distanceTarget", fixed(Math.max(0, data.distanceTarget)));
  setText("loss", fixed(data.totalLoss));
  setText("lossText", fixed(data.totalLoss));
  setText("targetWeight", fixed(data.targetWeight));
  setText("startWeightText", fixed(data.startWeight));
  setText("distanceText", fixed(Math.max(0, data.distanceTarget)));
  setText("progressPercent", `${fixed(data.progress, data.progress % 1 === 0 ? 0 : 1)}%`);
  setText("startDate", data.startDate);
  setText("endDate", data.endDate);
  setText("lastRecordDate", `更新于 ${formatDate(data.endDate)}`);
  element("ring").style.setProperty("--progress", `${Math.min(100, Math.max(0, data.progress))}%`);
  renderChange(data.changeFromLast);
  renderChart(data.records);
}

function renderChange(change: number) {
  const box = element("changeBox");
  const value = Math.abs(change).toFixed(1);
  if (change > 0) {
    box.className = "summary-value change-down";
    box.innerHTML = `<span class="arrow">↓</span>${value}<small>斤</small>`;
  } else if (change < 0) {
    box.className = "summary-value change-up";
    box.innerHTML = `<span class="arrow">↑</span>${value}<small>斤</small>`;
  } else {
    box.className = "summary-value";
    box.innerHTML = `<span class="arrow">—</span>0.0<small>斤</small>`;
  }
}

function renderChart(records: WeightRecord[]) {
  chart?.dispose();
  chart = echarts.init(element("chart"));
  const weights = records.map(item => Number(item.weight));
  chart.setOption({
    animationDuration: 900,
    grid: { top: 32, left: 8, right: 12, bottom: 26, containLabel: true },
    tooltip: {
      trigger: "axis",
      backgroundColor: "rgba(22, 46, 42, .94)",
      borderWidth: 0,
      padding: [10, 13],
      textStyle: { color: "#fff", fontSize: 12 },
      formatter(params: any) {
        const item = params[0];
        const record = records[item.dataIndex];
        const fatText = record ? `<br/>体脂：${fixed(record.fat)}%${record.fatEstimated ? "（估算）" : ""}` : "";
        return `${record.date}<br/><b style="font-size:16px">${fixed(item.value)} 斤</b>${fatText}`;
      }
    },
    xAxis: {
      type: "category", boundaryGap: false, data: records.map(item => item.date.slice(5).replace("-", ".")),
      axisTick: { show: false }, axisLine: { lineStyle: { color: "#dfe9e6" } },
      axisLabel: { color: "#93a29f", fontSize: 10, interval: "auto", margin: 12 }
    },
    yAxis: {
      type: "value",
      min: (value: { min: number }) => Math.floor(value.min - 3),
      max: (value: { max: number }) => Math.ceil(value.max + 3),
      axisLabel: { color: "#93a29f", fontSize: 10, formatter: "{value}" },
      splitLine: { lineStyle: { color: "#edf3f1", type: "dashed" } }
    },
    series: [{
      name: "体重", type: "line", smooth: 0.35, data: weights,
      symbol: "circle", symbolSize: 8, showSymbol: records.length <= 14,
      lineStyle: { width: 4, color: "#0eb395", cap: "round" },
      itemStyle: { color: "#0eb395", borderColor: "#fff", borderWidth: 2 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: "rgba(14, 179, 149, .28)" },
          { offset: 1, color: "rgba(14, 179, 149, .015)" }
        ])
      }
    }]
  });
}

function showState(state: "loading" | "error" | "content") {
  loading.classList.toggle("hidden", state !== "loading");
  error.classList.toggle("hidden", state !== "error");
  content.classList.toggle("hidden", state !== "content");
  element("app").setAttribute("aria-busy", String(state === "loading"));
}

function showError(message: string) {
  setText("errorMessage", message);
  showState("error");
}

function setText(id: string, value: string | number) { element(id).textContent = String(value); }
function element(id: string) { return document.getElementById(id) as HTMLElement; }
function fixed(value: number, digits = 1) { return Number(value || 0).toFixed(digits); }
function formatDate(date: string) { return date.replace(/-/g, "."); }
