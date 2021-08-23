package twasloglistener;

import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.ibm.websphere.management.AdminService;
import com.ibm.websphere.management.AdminServiceFactory;
import com.ibm.websphere.ras.RasMessage;

/**
 * https://www.ibm.com/docs/en/was/9.0.5?topic=logs-monitoring-application-logging-using-jmx-notifications
 * 
 * @author kevin.grigorenko@us.ibm.com
 */
@WebListener
public class LogListener implements ServletContextListener, NotificationListener {

	private ObjectName rasLoggingService;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println(toString() + " contextInitialized started");

		try {
			AdminService adminService = AdminServiceFactory.getAdminService();
			String cellName = adminService.getCellName();
			String nodeName = adminService.getNodeName();
			String serverName = adminService.getProcessName();
			String query = "WebSphere:cell=" + cellName + ",node=" + nodeName + ",process=" + serverName
					+ ",type=RasLoggingService,*";
			ObjectName queryName = new ObjectName(query);
			@SuppressWarnings("unchecked")
			Set<ObjectName> names = (Set<ObjectName>) adminService.queryNames(queryName, null);
			if (names.size() == 1) {
				ObjectName rasLoggingService = names.iterator().next();
				adminService.addNotificationListener(rasLoggingService, this, null, null);
				System.out
						.println(toString() + " contextInitialized successfully registered RasLoggingService listener");

			} else {
				throw new RuntimeException("Found " + names.size() + " RasLoggingService names instead of 1");
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}

		System.out.println(toString() + " contextInitialized finished");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println(toString() + " contextDestroyed started");

		try {
			if (rasLoggingService != null) {
				AdminServiceFactory.getAdminService().removeNotificationListener(rasLoggingService, this);
			}
		} catch (InstanceNotFoundException | ListenerNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println(toString() + " contextDestroyed finished");
	}

	@Override
	public void handleNotification(Notification notification, Object handback) {
		final RasMessage rasMessage = (RasMessage) notification.getUserData();
		final String message = rasMessage.getLocalizedMessage(null);
		if (message != null) {
			if (message.contains("WSVR0001I")) {
				System.out.println(toString() + " handleNotification received message: " + message);
			}
		}
	}
}
