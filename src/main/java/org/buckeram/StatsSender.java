package org.buckeram;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.websocket.Session;

import com.sun.management.OperatingSystemMXBean;

public class StatsSender implements Runnable
{
    private final OperatingSystemMXBean os;
    private final Session session;

    public StatsSender(final Session session)
    {
        this.session = session;
        this.os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public void run()
    {
        final StringBuilder json = new StringBuilder("{")
                .append(" \"usedMem\": ").append(getUsedMem())
                .append(", \"processLoad\": ").append(getProcessCpuLoad())
                .append(", \"systemLoad\": ").append(getSystemCpuLoad())
                .append(" }");
        System.out.printf("Sending %s to Session # %s\n", json, this.session.getId());
        if (this.session.isOpen())
        {
            try
            {
                this.session.getBasicRemote().sendText(json.toString());
            }
            catch (IOException e)
            {
                // Should probably deal with this a little better in real life...
                e.printStackTrace();
            }
        }
    }

    private String getUsedMem()
    {
        final long totalMem = os.getTotalPhysicalMemorySize();
        final long usedMem = totalMem - os.getFreePhysicalMemorySize();
        return String.format("%.1f", usedMem * 100.0 / totalMem);
    }

    private String getProcessCpuLoad()
    {
        final double pctLoad = os.getProcessCpuLoad() * 100;
        return String.format("%.1f", pctLoad);
    }

    private String getSystemCpuLoad()
    {
        final double pctLoad = os.getSystemCpuLoad() * 100;
        return String.format("%.1f", pctLoad);
    }
}
