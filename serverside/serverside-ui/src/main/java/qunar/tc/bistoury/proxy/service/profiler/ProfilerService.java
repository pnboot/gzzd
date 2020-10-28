package qunar.tc.bistoury.proxy.service.profiler;

import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.bean.ProfilerSettings;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author cai.wen created on 2019/10/30 14:50
 */
public interface ProfilerService {

    void startProfiler(String profilerId);

    String prepareProfiler(String agentId, ProfilerSettings profilerSettings);

    Profiler getProfilerRecord(String profilerId);

    void stopProfiler(String profilesId);

    void stopWithError(String profilerId);

    /**** copy form ui  ProfilerService *****/
    Profiler getRecord(String profilerId);

    List<Profiler> getLastRecords(String app, String agentId, LocalDateTime startTime);

    Optional<Profiler> getLastProfilerRecord(String app, String agentId);
}
