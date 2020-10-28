package qunar.tc.bistoury.proxy.web.controller.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.common.ProfilerUtil;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerDataManager;
import qunar.tc.bistoury.proxy.service.profiler.ProfilerService;
import qunar.tc.bistoury.proxy.util.profiler.ProfilerAnalyzer;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.util.ProxyInfo;
import qunar.tc.bistoury.ui.util.ProxyInfoParser;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static qunar.tc.bistoury.common.BistouryConstants.PROFILER_ROOT_PATH;


/**
 * @author cai.wen created on 2019/10/28 15:35
 */
@Controller
@RequestMapping("profiler")
public class ProfilerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfilerController.class);
    private static final ProxyInfo proxyInfo = new ProxyInfo("", -1, -1);


    private final ProfilerAnalyzer profilerAnalyzer = ProfilerAnalyzer.getInstance();

    @Resource
    private ProfilerService profilerService;
    @Resource
    private ProfilerDataManager profilerDataManager;

    @GetMapping("/get")
    @ResponseBody
    public Object requestProfiler(String profilerId) {
        return ResultHelper.success(profilerService.getRecord(profilerId));
    }

    @GetMapping("/analysis/info")
    @ResponseBody
    public Object analyzeProfilerState(String profilerId) {
        Optional<ProfilerInfoVo> proxyRef = analyze(profilerId);
        return ResultHelper.success(proxyRef.orElse(null));
    }

    @GetMapping("/records")
    @ResponseBody
    public Object lastThreeDaysProfiler(String appCode, String agentId) {
        List<Profiler> profilers = profilerService.getLastRecords(appCode, agentId, LocalDateTime.now().minusHours(3 * 24));
        profilers = profilers.stream()
                .filter(profiler -> notStartOrReadyState(profiler.getState()))
                .collect(Collectors.toList());
        return ResultHelper.success(profilers);
    }

    private boolean notStartOrReadyState(Profiler.State state) {
        return state != Profiler.State.start && state != Profiler.State.ready;
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/last")
    @ResponseBody
    public Object lastProfiler(String appCode, String agentId) {
        Optional<Profiler> profiler_ref = profilerService.getLastProfilerRecord(appCode, agentId);
        return profiler_ref.map(profiler ->
                ResultHelper.success(ImmutableMap.of("info", profiler, "curTime",
                        LocalDateTime.now().format(DATE_TIME_FORMATTER))))
                .orElseGet(ResultHelper::success);
    }

    private final TypeReference<ApiResult<Map<String, String>>> analyzerResponse = new TypeReference<ApiResult<Map<String, String>>>() {
    };

    private Optional<ProfilerInfoVo> analyze(String profilerId) {
        Optional<ProfilerInfoVo> profilerFileVoRef = getAnalyzedProxyForProfiler(profilerId);
        if (!profilerFileVoRef.isPresent()) {
            String agentId = profilerService.getRecord(profilerId).getAgentId();
            Profiler profiler = profilerService.getProfilerRecord(profilerId);
            profilerDataManager.requestData(profilerId, profiler.getAgentId());
            profilerAnalyzer.analyze(profilerId, profiler.getMode());

            return doAnalyze(agentId, profilerId);
        }
        return profilerFileVoRef;
    }

    private Optional<ProfilerInfoVo> doAnalyze(String agentId, String profilerId) {
        ProxyInfo proxyInfo = getProxyForAgent(agentId);
        try {
            String profilerFileName;
            try {
                Profiler profiler = profilerService.getProfilerRecord(profilerId);
                profilerDataManager.requestData(profilerId, profiler.getAgentId());
                profilerAnalyzer.analyze(profilerId, profiler.getMode());
                profilerFileName = profilerAnalyzer.renameProfilerDir(profilerId);
            } catch (Exception e) {
                LOGGER.error("analyze result error. profiler id: {}", profilerId);
                throw e;
            }

            Objects.requireNonNull(profilerFileName);
            return Optional.of(new ProfilerInfoVo(proxyInfo, profilerFileName, profilerService.getRecord(profilerId)));
        } catch (Exception e) {
            LOGGER.error("analyze profiler result error. profiler id: {}", profilerId, e);
            return Optional.empty();
        }
    }

    private Optional<ProfilerInfoVo> getAnalyzedProxyForProfiler(String profilerId) {
        String name = doGetName(profilerId);
        if (name != null) {
            Profiler profiler = profilerService.getRecord(profilerId);
            return Optional.of(new ProfilerInfoVo(proxyInfo, name, profiler));
        }
        return Optional.empty();
    }

    private String doGetName(String profilerId) {
        com.google.common.base.Optional<File> fileRef = ProfilerUtil.getProfilerDir(PROFILER_ROOT_PATH, profilerId);

        String name = null;
        try {
            if (fileRef.isPresent()) {
                name = fileRef.get().getName();
            }
        } catch (Exception e) {
            LOGGER.warn("get profiler error. profilerId: {}", profilerId);
            return null;
        }
        return name;
    }

    @GetMapping("/download")
    public void forwardSvgFile(@RequestParam("profilerId") String profilerId,
                               @RequestParam("name") String name,
                               @RequestParam("proxyUrl") String infoWithoutTomcatPort,
                               @RequestParam(value = "contentType", required = false) String contentType,
                               HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        contentType = Strings.isNullOrEmpty(contentType) ? "image/svg+xml" : contentType;
        response.setContentType(contentType);
        try (ServletOutputStream responseOutputStream = response.getOutputStream()) {
            Optional<ProxyInfo> proxyInfoRef = ProxyInfoParser.parseProxyInfoWithoutTomcatPort(infoWithoutTomcatPort);
            if (!proxyInfoRef.isPresent()) {
                return;
            }
            Path path = getFile(profilerId, name);
            ByteStreams.copy(Files.newInputStream(path), responseOutputStream);
            responseOutputStream.flush();
        }
    }
    private Path getFile(String profilerId, String svgName) {
        File profilerFile = ProfilerUtil.getProfilerDir(PROFILER_ROOT_PATH, profilerId).orNull();
        return Paths.get(Objects.requireNonNull(profilerFile).getAbsolutePath(), svgName);
    }


    private ProxyInfo getProxyForAgent(String agentId) {
        String ip = "";
        int tomcatPort = -1;
        int websocketPort = -1;

        return new ProxyInfo(ip, tomcatPort, websocketPort);
    }



    private static class ProfilerInfoVo {

        private ProxyInfo proxyInfo;
        private int realDuration;
        private Profiler profiler;
        private String eventType;

        public ProfilerInfoVo() {

        }

        public ProfilerInfoVo(ProxyInfo proxyInfo, String name, Profiler profiler) {
            this.proxyInfo = proxyInfo;
            this.realDuration = Integer.parseInt(name.split("-")[1]);
            if (profiler.getMode() == Profiler.Mode.async_sampler) {
                eventType = name.split("-")[2];
            }
            this.profiler = profiler;
        }

        public String getEventType() {
            return eventType;
        }

        public ProxyInfo getProxyInfo() {
            return proxyInfo;
        }

        public int getRealDuration() {
            return realDuration;
        }

        public Profiler getProfiler() {
            return profiler;
        }
    }
}
