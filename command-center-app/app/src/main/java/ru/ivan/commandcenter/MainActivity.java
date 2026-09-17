package ru.ivan.commandcenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
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
    private String currentPage = "home";
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
        header.addView(text("●  ONLINE", 12, GREEN, true));
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

        Button home = navButton("ГЛАВНАЯ", "home".equals(currentPage));
        Button projects = navButton("ПРОЕКТЫ", currentPage.startsWith("projects") || currentPage.startsWith("project_"));
        Button reports = navButton("ОТЧЁТЫ", "reports".equals(currentPage));
        Button agents = navButton("АГЕНТЫ", "agents".equals(currentPage));

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
        currentPage = "home";
        shell("Командный центр");

        LinearLayout commanderWrap = new LinearLayout(this);
        commanderWrap.setGravity(Gravity.CENTER);
        LinearLayout commander = new LinearLayout(this);
        commander.setOrientation(LinearLayout.VERTICAL);
        commander.setGravity(Gravity.CENTER);
        commander.setPadding(dp(16), dp(16), dp(16), dp(16));
        commander.setBackground(oval(GREEN, CARD, 2));
        commander.setClickable(true);
        commander.setFocusable(true);
        commander.setForeground(ripple());
        commander.setOnClickListener(v -> showCommanderDialog());
        TextView c = text("COMMANDER", 20, TEXT, true);
        c.setGravity(Gravity.CENTER);
        commander.addView(c);
        TextView ct = text("КООРДИНИРУЕТ", 11, GREEN, true);
        ct.setGravity(Gravity.CENTER);
        commander.addView(ct);
        commanderWrap.addView(commander, new LinearLayout.LayoutParams(dp(170), dp(170)));
        content.addView(commanderWrap);

        sectionTitle("АКТИВНЫЕ ПРОЕКТЫ", MUTED);
        LinearLayout projects = new LinearLayout(this);
        projects.setOrientation(LinearLayout.HORIZONTAL);
        projects.addView(projectMini("ROUTEMSK", "В РАБОТЕ", "78%", GREEN), weightCard());
        projects.addView(projectMini("БЛОГ", "ПЛАН", "34%", CYAN), weightCard());
        projects.addView(projectMini("КОЩЕЙ", "ПАУЗА", "18%", PURPLE), weightCard());
        content.addView(projects);

        sectionTitle("ТРЕБУЕТ РЕШЕНИЯ", AMBER);
        LinearLayout decision = card(AMBER);
        decision.addView(text("ROUTEMSK", 11, AMBER, true));
        decision.addView(text("Проверить ночной отчёт и следующий ход", 16, TEXT, true));
        decision.addView(text("Нажми, чтобы открыть отчёты →", 12, MUTED, false));
        makeInteractive(decision, v -> showReports());
        content.addView(decision);

        sectionTitle("ЖИВАЯ КОМАНДА", MUTED);
        Button refresh = actionButton("↻ Обновить статус команды");
        refresh.setOnClickListener(v -> loadRuntimeThenTeam());
        content.addView(refresh);
        loadRuntimeThenTeam();
    }

    private void showProjects() {
        currentPage = "projects";
        shell("Проекты");
        content.addView(projectCard("ROUTEMSK", "Сайт пропусков", "Отчёты и статус агента подключены", GREEN));
        content.addView(projectCard("БЛОГ", "Контент / соцсети", "Пока без фонового агента", CYAN));
        content.addView(projectCard("КОЩЕЙ", "Игра / Unity", "Пока без фонового агента", PURPLE));
    }

    private void showProjectDetail(String name) {
        currentPage = "project_" + name.toLowerCase();
        shell(name);

        if ("ROUTEMSK".equals(name)) {
            LinearLayout c = card(GREEN);
            c.addView(text("ROUTEMSK", 22, TEXT, true));
            c.addView(text("Сайт пропусков", 13, GREEN, true));
            c.addView(text("Подключены ночные отчёты, статус автономного агента и GitHub feed.", 13, MUTED, false));
            content.addView(c);

            Button site = actionButton("Открыть routemsk.ru →");
            site.setOnClickListener(v -> openUrl("https://routemsk.ru/"));
            content.addView(site);

            Button report = secondaryButton("Открыть ночные отчёты →", CYAN);
            report.setOnClickListener(v -> showReports());
            content.addView(report);

            Button agents = secondaryButton("Показать команду проекта →", GREEN);
            agents.setOnClickListener(v -> showAgents());
            content.addView(agents);
        } else if ("БЛОГ".equals(name)) {
            LinearLayout c = card(CYAN);
            c.addView(text("БЛОГ", 22, TEXT, true));
            c.addView(text("Контент / соцсети", 13, CYAN, true));
            c.addView(text("Фоновый агент ещё не подключён. Проект сохранён в штабе и готов к автоматизации.", 13, MUTED, false));
            content.addView(c);
            Button team = secondaryButton("Открыть команду →", CYAN);
            team.setOnClickListener(v -> showAgents());
            content.addView(team);
        } else {
            LinearLayout c = card(PURPLE);
            c.addView(text("КОЩЕЙ", 22, TEXT, true));
            c.addView(text("Игра / Unity", 13, PURPLE, true));
            c.addView(text("Проект на паузе. Фоновый агент пока не подключён.", 13, MUTED, false));
            content.addView(c);
            Button team = secondaryButton("Открыть команду →", PURPLE);
            team.setOnClickListener(v -> showAgents());
            content.addView(team);
        }
    }

    private void showAgents() {
        currentPage = "agents";
        shell("Команда");

        TextView explain = text("WORKING — есть реальная активность. IDLE — роль ждёт задачу. BLOCKED — есть настоящий блокер. MANUAL — внешний помощник запускается по твоей команде.", 13, MUTED, false);
        explain.setPadding(0, 0, 0, dp(10));
        content.addView(explain);

        Button refresh = actionButton("↻ Обновить команду");
        refresh.setOnClickListener(v -> loadRuntimeThenTeam());
        content.addView(refresh);
        loadRuntimeThenTeam();

        sectionTitle("AI HUB", MUTED);
        content.addView(chatGptCard());
        content.addView(linkCard("Gemini", "Исследование / SEO", "https://gemini.google.com/", CYAN));
        content.addView(linkCard("Claude", "UX / CRO / тексты", "https://claude.ai/", PURPLE));
    }

    private void showReports() {
        currentPage = "reports";
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
        final String pageAtRequest = currentPage;
        String cachedRuntime = prefs.getString("runtime", "");
        String cachedTeam = prefs.getString("team", "");
        if (!cachedRuntime.isEmpty()) parseRuntime(cachedRuntime);
        if (!cachedTeam.isEmpty() && currentPage.equals(pageAtRequest)) renderTeam(cachedTeam);

        fetch(RUNTIME_URL, "runtime", json -> {
            if (!currentPage.equals(pageAtRequest)) return;
            parseRuntime(json);
            fetch(TEAM_URL, "team", teamJson -> {
                if (currentPage.equals(pageAtRequest)) renderTeam(teamJson);
            });
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
        cc.addView(text("Нажми для деталей →", 10, statusColor(status), false));

        final String fName = name;
        final String fRole = role;
        final String fStatus = status;
        final String fTask = task;
        final String fHeartbeat = heartbeat;
        makeInteractive(cc, v -> showAgentDialog(fName, fRole, fStatus, fTask, fHeartbeat));
        return cc;
    }

    private void loadReport() {
        final String pageAtRequest = currentPage;
        String cached = prefs.getString("report", "");
        if (!cached.isEmpty() && currentPage.equals(pageAtRequest)) renderReport(cached);
        fetch(REPORT_URL, "report", json -> {
            if (currentPage.equals(pageAtRequest)) renderReport(json);
        });
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
            hero.addView(text("Нажми для полной сводки →", 10, reportColor(o.optString("status", "no_change")), false));
            makeInteractive(hero, v -> showReportSummaryDialog(o));
            content.addView(hero);

            addArrayCard("Сделано", o.optJSONArray("completed"));
            addArrayCard("Изменено", o.optJSONArray("changed"));

            JSONArray checks = o.optJSONArray("checks");
            LinearLayout checkCard = card(CYAN);
            checkCard.setTag("report_dynamic");
            checkCard.addView(text("Проверки", 16, CYAN, true));
            if (checks == null || checks.length() == 0) {
                checkCard.addView(text("—", 13, MUTED, false));
            } else {
                for (int i = 0; i < checks.length(); i++) {
                    JSONObject c = checks.getJSONObject(i);
                    String r = c.optString("result", "unknown");
                    checkCard.addView(text(checkIcon(r) + " " + c.optString("name") + " — " + c.optString("details"), 13, checkColor(r), false));
                }
            }
            final JSONArray fChecks = checks;
            makeInteractive(checkCard, v -> showJsonArrayDialog("Проверки", fChecks));
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
        LinearLayout c = card(CYAN);
        c.setTag("report_dynamic");
        c.addView(text(title, 16, CYAN, true));
        if (arr == null || arr.length() == 0) c.addView(text("—", 13, MUTED, false));
        else for (int i = 0; i < arr.length(); i++) c.addView(text("• " + arr.optString(i), 13, TEXT, false));
        c.addView(text("Нажми для просмотра →", 10, CYAN, false));
        makeInteractive(c, v -> showJsonArrayDialog(title, arr));
        content.addView(c);
    }

    private View projectMini(String name, String state, String pct, int color) {
        LinearLayout c = card(color);
        c.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView pctView = text(pct, 14, TEXT, true);
        pctView.setGravity(Gravity.CENTER);
        pctView.setBackground(oval(color, Color.TRANSPARENT, 4));
        c.addView(pctView, new LinearLayout.LayoutParams(dp(54), dp(54)));

        TextView n = text(name, 12, TEXT, true);
        n.setGravity(Gravity.CENTER_HORIZONTAL);
        n.setPadding(0, dp(6), 0, 0);
        c.addView(n);

        TextView st = text(state, 9, color, true);
        st.setGravity(Gravity.CENTER_HORIZONTAL);
        c.addView(st);

        makeInteractive(c, v -> showProjectDetail(name));
        return c;
    }

    private View projectCard(String name, String subtitle, String detail, int color) {
        LinearLayout c = card(color);
        c.addView(text(name, 19, TEXT, true));
        c.addView(text(subtitle, 13, color, true));
        c.addView(text(detail, 13, MUTED, false));
        c.addView(text("Открыть проект →", 11, color, true));
        makeInteractive(c, v -> showProjectDetail(name));
        return c;
    }

    private View chatGptCard() {
        LinearLayout c = card(GREEN);
        c.addView(text("ChatGPT", 18, TEXT, true));
        c.addView(text("Командование / оркестрация", 13, MUTED, false));
        Button b = actionButton("Открыть приложение →");
        b.setOnClickListener(v -> openChatGPTApp());
        c.addView(b);
        makeInteractive(c, v -> openChatGPTApp());
        return c;
    }

    private View linkCard(String name, String role, String url, int color) {
        LinearLayout c = card(color);
        c.addView(text(name, 18, TEXT, true));
        c.addView(text(role, 13, MUTED, false));
        Button b = secondaryButton("Открыть →", color);
        b.setOnClickListener(v -> openUrl(url));
        c.addView(b);
        makeInteractive(c, v -> openUrl(url));
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

    private void openChatGPTApp() {
        try {
            Intent launch = getPackageManager().getLaunchIntentForPackage("com.openai.chatgpt");
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(launch);
                return;
            }

            Intent direct = new Intent(Intent.ACTION_VIEW, Uri.parse("https://chatgpt.com/"));
            direct.setPackage("com.openai.chatgpt");
            startActivity(direct);
        } catch (Exception e) {
            Toast.makeText(this, "Приложение ChatGPT не найдено — открываю веб-версию", Toast.LENGTH_SHORT).show();
            openUrl("https://chatgpt.com/");
        }
    }

    private void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCommanderDialog() {
        showDialog("COMMANDER", "Главный управляющий агент. Принимает цель, разбивает её на задачи, назначает исполнителей, собирает результаты и поднимает тебе только решения, где реально нужен человек.");
    }

    private void showAgentDialog(String name, String role, String status, String task, String heartbeat) {
        StringBuilder b = new StringBuilder();
        b.append(role).append("\n\nСтатус: ").append(statusLabel(status));
        if (task != null && !task.isEmpty()) b.append("\n\nТекущая задача:\n").append(task);
        if (heartbeat != null && !heartbeat.isEmpty()) b.append("\n\nHeartbeat:\n").append(heartbeat);
        showDialog(name, b.toString());
    }

    private void showReportSummaryDialog(JSONObject o) {
        StringBuilder b = new StringBuilder();
        b.append(o.optString("summary", "Нет сводки"));
        b.append("\n\nПоследний запуск: ").append(o.optString("run_at", "—"));
        b.append("\nСтатус: ").append(o.optString("status", "no_change"));
        showDialog("ROUTEMSK // NIGHT SHIFT", b.toString());
    }

    private void showJsonArrayDialog(String title, JSONArray arr) {
        if (arr == null || arr.length() == 0) {
            showDialog(title, "Пока пусто.");
            return;
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < arr.length(); i++) {
            Object item = arr.opt(i);
            if (item instanceof JSONObject) {
                JSONObject o = (JSONObject) item;
                b.append("• ").append(o.optString("name", "Проверка"));
                String result = o.optString("result", "unknown");
                if (!result.isEmpty()) b.append(" — ").append(result.toUpperCase());
                String details = o.optString("details", "");
                if (!details.isEmpty()) b.append("\n  ").append(details);
            } else {
                b.append("• ").append(String.valueOf(item));
            }
            if (i < arr.length() - 1) b.append("\n\n");
        }
        showDialog(title, b.toString());
    }

    private void showDialog(String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Закрыть", null)
                .create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(GREEN);
        });
        dialog.show();
    }

    private LinearLayout card(int accent) {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(14), dp(12), dp(14), dp(12));
        c.setBackground(roundRect(CARD, accent, 1, 16));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(6), dp(6), dp(6));
        c.setLayoutParams(p);
        return c;
    }

    private void makeInteractive(View v, View.OnClickListener listener) {
        v.setClickable(true);
        v.setFocusable(true);
        v.setForeground(ripple());
        v.setOnClickListener(listener);
    }

    private android.graphics.drawable.Drawable ripple() {
        TypedValue outValue = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true) && outValue.resourceId != 0) {
            return getDrawable(outValue.resourceId);
        }
        return null;
    }

    private GradientDrawable roundRect(int fill, int stroke, int strokeDp, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setColor(fill);
        d.setCornerRadius(dp(radiusDp));
        d.setStroke(dp(strokeDp), stroke);
        return d;
    }

    private GradientDrawable oval(int stroke, int fill, int strokeDp) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(fill);
        d.setStroke(dp(strokeDp), stroke);
        return d;
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
        b.setBackground(roundRect(GREEN, GREEN, 1, 12));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(54));
        p.setMargins(0, dp(6), 0, dp(6));
        b.setLayoutParams(p);
        return b;
    }

    private Button secondaryButton(String s, int color) {
        Button b = new Button(this);
        b.setText(s);
        b.setAllCaps(false);
        b.setTextColor(color);
        b.setTextSize(13);
        b.setBackground(roundRect(CARD, color, 1, 12));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(52));
        p.setMargins(0, dp(6), 0, dp(6));
        b.setLayoutParams(p);
        return b;
    }

    private Button navButton(String s, boolean active) {
        Button b = new Button(this);
        b.setText(s);
        b.setAllCaps(false);
        b.setTextColor(active ? GREEN : MUTED);
        b.setTextSize(10);
        b.setTypeface(Typeface.DEFAULT, active ? Typeface.BOLD : Typeface.NORMAL);
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
