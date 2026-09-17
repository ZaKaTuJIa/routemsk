package ru.ivan.commandcenter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    private static final String TEAM_URL = "https://raw.githubusercontent.com/ZaKaTuJIa/routemsk/command-center-reports/reports/command-center/team.json";
    private static final String REPORT_URL = "https://raw.githubusercontent.com/ZaKaTuJIa/routemsk/command-center-reports/reports/routemsk/latest.json";
    private static final String RUNTIME_URL = "https://raw.githubusercontent.com/ZaKaTuJIa/routemsk/command-center-reports/reports/routemsk/runtime.json";

    private static final int BG = Color.rgb(4, 10, 15);
    private static final int CARD = Color.rgb(12, 23, 31);
    private static final int TEXT = Color.rgb(242, 248, 252);
    private static final int MUTED = Color.rgb(138, 157, 173);
    private static final int GREEN = Color.rgb(93, 255, 176);
    private static final int CYAN = Color.rgb(73, 179, 255);
    private static final int PURPLE = Color.rgb(179, 99, 255);
    private static final int AMBER = Color.rgb(255, 191, 73);
    private static final int RED = Color.rgb(255, 92, 112);
    private static final int GRAY = Color.rgb(112, 132, 148);

    private LinearLayout content;
    private SharedPreferences prefs;
    private String runtimeState = "idle";
    private String runtimeTask = "";
    private String runtimeHeartbeat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("cc_cache", MODE_PRIVATE);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        showHome();
    }

    private void shell(String title) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(dp(18), dp(14), dp(18), dp(10));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);
        brand.addView(text("IVAN", 28, TEXT, true));
        brand.addView(text("COMMAND CENTER", 13, GREEN, true));
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1f));
        TextView online = text("●  CONTROL", 12, GREEN, true);
        header.addView(online);
        root.addView(header);

        TextView page = text(title, 24, TEXT, true);
        page.setPadding(0, dp(20), 0, dp(10));
        root.addView(page);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setPadding(0, dp(8), 0, 0);
        Button home = navButton("ГЛАВНАЯ");
        Button projects = navButton("ПРОЕКТЫ");
        Button reports = navButton("ОТЧЁТЫ");
        Button agents = navButton("АГЕНТЫ");
        home.setOnClickListener(v -> showHome());
        projects.setOnClickListener(v -> showProjects());
        reports.setOnClickListener(v -> showReports());
        agents.setOnClickListener(v -> showAgents());
        nav.addView(home, weight());
        nav.addView(projects, weight());
        nav.addView(reports, weight());
        nav.addView(agents, weight());
        root.addView(nav);

        setContentView(root);
    }

    private void showHome() {
        shell("Командный центр");
        LinearLayout commander = card(GREEN);
        TextView c = text("COMMANDER", 23, TEXT, true);
        c.setGravity(Gravity.CENTER_HORIZONTAL);
        commander.addView(c);
        TextView ct = text("КООРДИНИРУЕТ", 12, GREEN, true);
        ct.setGravity(Gravity.CENTER_HORIZONTAL);
        commander.addView(ct);
        content.addView(commander);

        sectionTitle("АКТИВНЫЕ ПРОЕКТЫ", MUTED);
        LinearLayout projects = new LinearLayout(this);
        projects.setOrientation(LinearLayout.HORIZONTAL);
        projects.addView(projectMini("ROUTEMSK", "В РАБОТЕ", "78%", GREEN), weightCard());
        projects.addView(projectMini("БЛОГ", "ПЛАН", "34%", CYAN), weightCard());
        projects.addView(projectMini("КОЩЕЙ", "ПАУЗА", "18%", PURPLE), weightCard());
        content.addView(projects);

        sectionTitle("ЖИВАЯ КОМАНДА", MUTED);
        Button refresh = actionButton("↻ Обновить статус команды");
        refresh.setOnClickListener(v -> loadRuntimeThenTeam());
        content.addView(refresh);
        loadRuntimeThenTeam();
    }

    private void showProjects() {
        shell("Проекты");
        content.addView(projectCard("ROUTEMSK", "Сайт пропусков", "Отчёты и статус агента подключены", GREEN));
        content.addView(projectCard("БЛОГ", "Контент / соцсети", "Пока без фонового агента", CYAN));
        content.addView(projectCard("КОЩЕЙ", "Игра / Unity", "Пока без фонового агента", PURPLE));
    }

    private void showAgents() {
        shell("Команда");
        TextView explain = text("WORKING — есть реальная активность. IDLE — роль ждёт задачу. BLOCKED — есть настоящий блокер. MANUAL — внешний помощник запускается только по твоей команде.", 13, MUTED, false);
        explain.setPadding(0, 0, 0, dp(10));
        content.addView(explain);
        Button refresh = actionButton("↻ Обновить команду");
        refresh.setOnClickListener(v -> loadRuntimeThenTeam());
        content.addView(refresh);
        loadRuntimeThenTeam();

        sectionTitle("AI HUB", MUTED);
        content.addView(linkCard("ChatGPT", "Командование / оркестрация", "https://chatgpt.com/", GREEN));
        content.addView(linkCard("Gemini", "Исследование / SEO", "https://gemini.google.com/", CYAN));
        content.addView(linkCard("Claude", "UX / CRO / тексты", "https://claude.ai/", PURPLE));
    }

    private void showReports() {
        shell("Ночные отчёты");
        TextView explain = text("ROUTEMSK Night Shift: только факты — что реально сделано, что изменено, какие проверки прошли и что заблокировано.", 13, MUTED, false);
        explain.setPadding(0, 0, 0, dp(10));
        content.addView(explain);
        Button refresh = actionButton("↻ Обновить отчёт");
        refresh.setOnClickListener(v -> loadReport());
        content.addView(refresh);
        loadReport();
    }

    private void loadRuntimeThenTeam() {
        String cachedRuntime = prefs.getString("runtime", "");
        String cachedTeam = prefs.getString("team", "");
        if (!cachedRuntime.isEmpty()) parseRuntime(cachedRuntime);
        if (!cachedTeam.isEmpty()) renderTeam(cachedTeam);
        fetch(RUNTIME_URL, "runtime", json -> {
            parseRuntime(json);
            fetch(TEAM_URL, "team", this::renderTeam);
        });
    }

    private void parseRuntime(String json) {
        try {
            JSONObject o = new JSONObject(json);
            runtimeState = o.optString("state", "idle");
            runtimeTask = o.optString("current_task", "");
            runtimeHeartbeat = o.optString("heartbeat_at", "");
        } catch (Exception ignored) {}
    }

    private void renderTeam(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray members = root.optJSONArray("members");
            if (members == null) return;
            removeDynamicTeamCards();
            for (int i = 0; i < members.length(); i++) {
                JSONObject m = members.getJSONObject(i);
                String id = m.optString("id");
                String status = m.optString("status", "idle");
                String task = m.optString("current_task", "");
                String heartbeat = "";
                if ("routemsk-night-shift".equals(id)) {
                    status = runtimeState;
                    if (!runtimeTask.isEmpty()) task = runtimeTask;
                    heartbeat = runtimeHeartbeat;
                }
                LinearLayout cc = memberCard(m.optString("name"), m.optString("role"), status, task, heartbeat);
                cc.setTag("team_dynamic");
                content.addView(cc);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Team feed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeDynamicTeamCards() {
        for (int i = content.getChildCount() - 1; i >= 0; i--) {
            View v = content.getChildAt(i);
            Object tag = v.getTag();
            if (tag != null && "team_dynamic".equals(tag.toString())) content.removeViewAt(i);
        }
    }

    private LinearLayout memberCard(String name, String role, String status, String task, String heartbeat) {
        LinearLayout cc = card(statusColor(status));
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(text("●", 17, statusColor(status), true));
        TextView n = text("  " + name, 16, TEXT, true);
        row.addView(n, new LinearLayout.LayoutParams(0, -2, 1f));
        row.addView(text(statusLabel(status), 11, statusColor(status), true));
        cc.addView(row);
        cc.addView(text(role, 12, MUTED, false));
        if (task != null && !task.isEmpty()) cc.addView(text(task, 13, TEXT, false));
        if (heartbeat != null && !heartbeat.isEmpty()) cc.addView(text("heartbeat: " + heartbeat, 10, MUTED, false));
        return cc;
    }

    private void loadReport() {
        String cached = prefs.getString("report", "");
        if (!cached.isEmpty()) renderReport(cached);
        fetch(REPORT_URL, "report", this::renderReport);
    }

    private void renderReport(String json) {
        try {
            JSONObject o = new JSONObject(json);
            removeDynamicReportCards();
            LinearLayout hero = card(reportColor(o.optString("status", "no_change")));
            hero.setTag("report_dynamic");
            hero.addView(text("ROUTEMSK // NIGHT SHIFT", 17, TEXT, true));
            hero.addView(text(o.optString("summary", "Нет сводки"), 14, TEXT, false));
            hero.addView(text("Последний запуск: " + o.optString("run_at", "—"), 11, MUTED, false));
            content.addView(hero);
            addArrayCard("Сделано", o.optJSONArray("completed"));
            addArrayCard("Изменено", o.optJSONArray("changed"));
            JSONArray checks = o.optJSONArray("checks");
            LinearLayout checkCard = card(CYAN); checkCard.setTag("report_dynamic");
            checkCard.addView(text("Проверки", 16, CYAN, true));
            if (checks == null || checks.length() == 0) checkCard.addView(text("—", 13, MUTED, false));
            else for (int i = 0; i < checks.length(); i++) {
                JSONObject c = checks.getJSONObject(i);
                String r = c.optString("result", "unknown");
                checkCard.addView(text(checkIcon(r) + " " + c.optString("name") + " — " + c.optString("details"), 13, checkColor(r), false));
            }
            content.addView(checkCard);
            addArrayCard("Блокеры", o.optJSONArray("blockers"));
            addArrayCard("Следующий ход", o.optJSONArray("next"));
        } catch (Exception e) {
            Toast.makeText(this, "Report feed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeDynamicReportCards() {
        for (int i = content.getChildCount() - 1; i >= 0; i--) {
            View v = content.getChildAt(i);
            Object tag = v.getTag();
            if (tag != null && "report_dynamic".equals(tag.toString())) content.removeViewAt(i);
        }
    }

    private void addArrayCard(String title, JSONArray arr) {
        LinearLayout c = card(CYAN); c.setTag("report_dynamic");
        c.addView(text(title, 16, CYAN, true));
        if (arr == null || arr.length() == 0) c.addView(text("—", 13, MUTED, false));
        else for (int i = 0; i < arr.length(); i++) c.addView(text("• " + arr.optString(i), 13, TEXT, false));
        content.addView(c);
    }

    private View projectMini(String name, String state, String pct, int color) {
        LinearLayout c = card(color);
        c.addView(text(pct, 16, TEXT, true));
        c.addView(text(name, 13, TEXT, true));
        c.addView(text(state, 10, color, true));
        return c;
    }

    private View projectCard(String name, String subtitle, String detail, int color) {
        LinearLayout c = card(color);
        c.addView(text(name, 19, TEXT, true));
        c.addView(text(subtitle, 13, color, true));
        c.addView(text(detail, 13, MUTED, false));
        return c;
    }

    private View linkCard(String name, String role, String url, int color) {
        LinearLayout c = card(color);
        c.addView(text(name, 18, TEXT, true));
        c.addView(text(role, 13, MUTED, false));
        Button b = actionButton("Открыть →");
        b.setOnClickListener(v -> openUrl(url));
        c.addView(b);
        return c;
    }

    private void fetch(String url, String cacheKey, Result result) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.setRequestProperty("Accept", "application/json");
                int code = connection.getResponseCode();
                if (code < 200 || code >= 300) throw new Exception("HTTP " + code);
                String body = readAll(connection.getInputStream());
                prefs.edit().putString(cacheKey, body).apply();
                runOnUiThread(() -> result.ok(body));
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Сеть недоступна — оставил последний кэш", Toast.LENGTH_SHORT).show());
            } finally {
                if (connection != null) connection.disconnect();
            }
        }).start();
    }

    private String readAll(InputStream in) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line).append('\n');
        br.close();
        return sb.toString();
    }

    private void openUrl(String url) {
        try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); }
        catch (Exception e) { Toast.makeText(this, "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show(); }
    }

    private LinearLayout card(int accent) {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(14), dp(12), dp(14), dp(12));
        c.setBackgroundColor(CARD);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(6), dp(6), dp(6));
        c.setLayoutParams(p);
        return c;
    }

    private void sectionTitle(String s, int color) {
        TextView t = text(s, 15, color, true);
        t.setPadding(0, dp(18), 0, dp(6));
        content.addView(t);
    }

    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextColor(color);
        t.setTextSize(size);
        t.setLineSpacing(0, 1.08f);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private Button actionButton(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setAllCaps(false);
        b.setTextColor(BG);
        b.setTextSize(13);
        b.setBackgroundColor(GREEN);
        return b;
    }

    private Button navButton(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setAllCaps(false);
        b.setTextColor(MUTED);
        b.setTextSize(10);
        b.setBackgroundColor(BG);
        return b;
    }

    private LinearLayout.LayoutParams weight() {
        return new LinearLayout.LayoutParams(0, dp(48), 1f);
    }

    private LinearLayout.LayoutParams weightCard() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -2, 1f);
        p.setMargins(0, 0, dp(5), 0);
        return p;
    }

    private int dp(int n) {
        return (int)(n * getResources().getDisplayMetrics().density + 0.5f);
    }

    private int statusColor(String status) {
        if ("working".equals(status)) return GREEN;
        if ("blocked".equals(status)) return RED;
        if ("manual".equals(status)) return AMBER;
        return GRAY;
    }

    private String statusLabel(String status) {
        if ("working".equals(status)) return "WORKING";
        if ("blocked".equals(status)) return "BLOCKED";
        if ("manual".equals(status)) return "MANUAL";
        return "IDLE";
    }

    private int reportColor(String status) {
        if ("progress".equals(status)) return GREEN;
        if ("blocked".equals(status)) return RED;
        return GRAY;
    }

    private int checkColor(String result) {
        if ("pass".equals(result)) return GREEN;
        if ("fail".equals(result)) return RED;
        return AMBER;
    }

    private String checkIcon(String result) {
        if ("pass".equals(result)) return "✓";
        if ("fail".equals(result)) return "✕";
        return "?";
    }

    private interface Result { void ok(String body); }
}
