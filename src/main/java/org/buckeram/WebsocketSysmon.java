package org.buckeram;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/sysmon")
public class WebsocketSysmon
{
    private final ScheduledExecutorService scheduler;

    public WebsocketSysmon()
    {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        System.out.println("New WebsocketSysmon endpoint created: " + this);
    }

    @OnOpen
    public void onOpen(final Session session) throws IOException
    {
        final String sessionId = session.getId();
        System.out.println("Session opened: " + sessionId);
        final StatsSender statsSender = new StatsSender(session);
        this.scheduler.scheduleAtFixedRate(statsSender, 0L, 1L, TimeUnit.SECONDS);
    }

    @OnClose
    public void onClose(final Session session)
    {
        this.scheduler.shutdown();
        System.out.println("Closing Session: " + session.getId());
    }

    @OnError
    public void onError(final Session session, final Throwable t)
    {
        System.out.printf("Oh dear, Session %s ran into an exception: %s\n",
                session.getId(), t);
        t.printStackTrace();
    }
}
