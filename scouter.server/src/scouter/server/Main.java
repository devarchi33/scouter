/*
 *  Copyright 2015 LG CNS.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package scouter.server;

import java.io.File;

import scouter.Version;
import scouter.server.account.AccountManager;
import scouter.server.core.AutoDeleteScheduler;
import scouter.server.core.TextCacheReset;
import scouter.server.netio.data.NetDataProcessor;
import scouter.server.netio.data.net.DataUdpServer;
import scouter.server.netio.service.ServiceHandlingProxy;
import scouter.server.netio.service.net.ServiceServer;
import scouter.server.plugin.PlugInManager;
import scouter.util.SysJMX;
import scouter.util.ThreadUtil;
import scouter.util.logo.Logo;

public class Main {

	public static void main(String[] args) {

		Logo.print(true);
		Logger.println("Scouter Server Version " + Version.getServerFullVersion());
		Logo.print(Logger.pw, true);

		Configure.getInstance();
		CounterManager.getInstance();
		AccountManager.ACCOUNT_FILENAME();

		DataUdpServer.conf();

		ServiceHandlingProxy.load();
		ServiceServer.conf();

		PlugInManager.getInstance();

		NetDataProcessor.working();

		AutoDeleteScheduler.getInstance();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				ShutdownManager.shutdown();
			}
		});

		File exit = new File(SysJMX.getProcessPID() + ".scouter");
		try {
			exit.createNewFile();
		} catch (Exception e) {
			String tmp = System.getProperty("user.home", "/tmp");
			exit = new File(tmp, SysJMX.getProcessPID() + ".scouter.run");
			try {
				exit.createNewFile();
			} catch (Exception k) {
				System.exit(1);
			}
		}
		exit.deleteOnExit();

		TextCacheReset.engine();

		System.out.println("System JRE version : " + System.getProperty("java.version"));
		System.out.println("This product includes GeoLite data created by MaxMind, available from");
		System.out.println("http://www.maxmind.com");
		System.out.println("download:  http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz");
		System.out.println("add configure:  geoip_data_city=<download path>/GeoLiteCity.dat");

		while (true) {
			if (exit.exists() == false) {
				ShutdownManager.shutdown();
				System.exit(0);
			}
			ThreadUtil.sleep(1000);
		}

	}
}