package launch

import org.apache.catalina.connector.Connector
import org.apache.catalina.startup.Tomcat
import org.apache.coyote.http11.Http11NioProtocol
import org.apache.naming.resources.VirtualDirContext

import java.util.logging.Level

/**
 * @author yinheli
 */
class Server {

    public void start() {
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING)
        System.setProperty 'catalina.home', '.tomcat'
        def tomcat = new Tomcat()
        tomcat.silent = true
        tomcat.engine.name = 'postool'
        def connector = new Connector(Http11NioProtocol.name)
        connector.port = Integer.getInteger('port', 8095)

        connector.setAttribute 'backlog'              , 200
        connector.setAttribute 'minSpareThreads'      , 1
        connector.setAttribute 'maxThreads'           , 200
        connector.setAttribute 'pollerThreadCount'    , 2
        connector.setAttribute 'maxKeepAliveRequests' , 100
        connector.setAttribute 'socketBuffer'         , 1024 * 2
        connector.setAttribute 'maxSavePostSize'      , 1024 * 8
        connector.setAttribute 'maxPostSize'          , 1024 * 1024 * 10
        connector.setAttribute 'connectionTimeout'    , 2 * 60 * 1000
        connector.setAttribute 'tcpNoDelay'           , true
        connector.setAttribute 'socket.reuseAddress'  , true
        connector.URIEncoding = 'UTF-8'
        connector.xpoweredBy  = false
        tomcat.setConnector         connector
        tomcat.service.addConnector connector
        def ctx = tomcat.addWebapp('/', new File('webapp').absolutePath)
        ctx.reloadable        = false
        ctx.cookies           = false
        ctx.sessionTimeout    = 30
        ctx.sessionCookieName = 'postool_sid'
        def res = new VirtualDirContext()
        res.setExtraResourcePaths("/WEB-INF/classes=${new File('classes').absolutePath}")
        ctx.setResources(res)
        tomcat.start()
        tomcat.getServer().await()
    }

    public static void main(String[] args) {
        new Server().start()
    }

}
