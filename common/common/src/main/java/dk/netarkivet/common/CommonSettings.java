/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 * The Netarchive Suite - Software to harvest and preserve websites
 * Copyright 2004-2007 Det Kongelige Bibliotek and Statsbiblioteket, Denmark
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package dk.netarkivet.common;

import java.util.Arrays;
import java.util.List;

import dk.netarkivet.common.utils.Settings;

/**
 * Settings common to the entire NetarchiveSuite.
 */
public class CommonSettings {
    /** The default place in classpath where the settings file can be found. */
    private static final String DEFAULT_SETTINGS_CLASSPATH
            = "dk/netarkivet/common/settings.xml";

    static {
        Settings.addDefaultClasspathSettings(
                DEFAULT_SETTINGS_CLASSPATH
        );
    }

    /**
     * The fields of this class that don't actually correspond to settings,
     * or are pluggable settings not always present.
     */
    public static List<String> EXCLUDED_FIELDS = Arrays.asList(
            "DEFAULT_SETTINGS_CLASSPATH");

    /* The setting names used should be declared and documented here */

    /** Common temporary directory for all applications. */
    public static String DIR_COMMONTEMPDIR = "settings.common.tempDir";

    /** The class to use for RemoteFile objects. */
    public static String REMOTE_FILE_CLASS
            = "settings.common.remoteFile.class";

    /** Selects the broker class to be used. Must be subclass of
     * dk.netarkivet.common.distribute.JMSConnection. */
    public static String JMS_BROKER_CLASS = "settings.common.jms.class";

    /**
     * The name of the environment in which this code is running, e.g.
     * PROD, RELEASETEST, NHC, ... Common prefix to all JMS channels.
     */
    public static String ENVIRONMENT_NAME
            = "settings.common.jms.environmentName";

    /**
     * The *unique* (per host) port number that may or may not be used to serve
     * http, but is frequently used to identify the process.
     */
    public static String HTTP_PORT_NUMBER = "settings.common.http.port";

    /**
     * The class that implements the ArcRepositoryClient.  This class will
     * be instantiated by the ArcRepositoryClientFactory.
     */
    public static String ARC_REPOSITORY_CLIENT
            = "settings.common.arcrepositoryClient.class";

    /**
     * The class instantiated to give access to indices.  Will be created
     * by IndexClientFactory.
     */
    public static String INDEXSERVER_CLIENT
            = "settings.common.indexClient.class";

    /**
     * The name of the directory where cache data global to the entire
     * machine can be stored.  Various kinds of caches should be stored in
     * subdirectories of this.
     */
    public static String CACHE_DIR = "settings.common.cacheDir";

    // TODO: Currently only used by heritrix shutdown - move to harvester
    // settings?
    /** The number of milliseconds we wait for processes to react to
     *  shutdown requests. */
    public static String PROCESS_TIMEOUT
            = "settings.common.processTimeout";

    // TODO: Currently only used by harvestscheduler - move to harvester
    // settings?
    /** The class that defines DB-specific methods */
    public static String DB_SPECIFICS_CLASS
            = "settings.common.database.specificsclass";

    /**
     * URL to use to connect to the database.  If absent or empty, the URL
     * will be constructed in a derby-specific way based on DB_NAME and
     * HARVESTDEFINITION_BASEDIR.
     */
    public static String DB_URL
            = "settings.common.database.url";

    /**
     * The earliest time of day backup will be initiated, 0..24 hours.  At
     * a time shortly after this, a consistent backup copy of the database
     * will be created.
     */
    public static String DB_BACKUP_INIT_HOUR
            = "settings.common.database.backupInitHour";


    /**
     * The subclass of SiteSection that defines a part of the
     * web interface.
     */
    public static String SITESECTION_CLASS
            = "settings.common.webinterface.siteSection.class";

    /**
     * The directory or war-file containing the web application
     * for a site section.
     */
    public static String SITESECTION_WEBAPPLICATION
            = "settings.common.webinterface.siteSection.webapplication";

    /** The URL path for this site section. */
    public static String SITESECTION_DEPLOYPATH
            = "settings.common.webinterface.siteSection.deployPath";

    /** The entire webinterface structure */
    public static String WEBINTERFACE_SETTINGS
            = "settings.common.webinterface";

    /**
     * The names of all bit archive locations in the
     * environment, e.g., "KB" and "SB".
     */
    public static String ENVIRONMENT_LOCATION_NAMES
            = "settings.common.locations.location.name";

    /** Default bit archive to use for batch jobs (if none is specified) */
    public static String ENVIRONMENT_BATCH_LOCATION
            = "settings.common.locations.batchLocation";

    /** For archiving applications, which bit archive are you part of? */
    public static String ENVIRONMENT_THIS_LOCATION
            = "settings.common.thisPhysicalLocation";

    /** The name of the application, fx. "BitarchiveServerApplication". */
    public static String APPLICATIONNAME
            = "settings.common.monitorApplicationName";

    /**
     * The mail server to use when sending mails. Currently only used for
     * email notifications.
     */
    public static String MAIL_SERVER = "settings.common.mail.server";

    /** The receiver of email notifications. */
    public static String MAIL_RECEIVER
            = "settings.common.notifications.receiver";

    /** The sender of email notifications. */
    public static String MAIL_SENDER
            = "settings.common.notifications.sender";

    /** The implementation class for notifications. */
    public static String NOTIFICATIONS_CLASS
            = "settings.common.notifications.class";

    /** Which port to use for JMX. */
    public static String JMX_PORT = "settings.common.jmx.port";

    /** Which port to use for JMX's RMI communication. */
    public static String JMX_RMI_PORT = "settings.common.jmx.rmiPort";

    /** Which file to look for JMX passwords in. */
    public static String JMX_PASSWORD_FILE
            = "settings.common.jmx.passwordFile";

    /** How many seconds we will wait before giving up on a JMX connection. */
    public static String JMX_TIMEOUT
            = "settings.common.jmx.timeout";

    /** Which class to use for monitor registry. Must implement the interface
     * dk.netarkivet.common.distribute.monitorregistry.MonitorRegistryClient. */
    public static String MONITOR_REGISTRY_CLIENT
            = "settings.common.monitorregistryClient.class";

    /** Valid top level domains, like .co.uk, .dk, .org. Repeats. */
    public static String TLDS = "settings.common.topLevelDomains.tld";
    /**
     * When the length record exceeds this number, the contents of the record
     * will be transferred using a RemoteFile. Currently set to 31 MB
     * ( Integer.MAX_VALUE / 64) -->
     */
    public static String BITARCHIVE_LIMIT_FOR_RECORD_DATATRANSFER_IN_FILE
            = "settings.common.repository.limitForRecordDatatransferInFile";
}
