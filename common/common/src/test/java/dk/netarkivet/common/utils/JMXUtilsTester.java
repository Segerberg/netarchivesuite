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
package dk.netarkivet.common.utils;

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

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

import junit.framework.TestCase;

import dk.netarkivet.common.Settings;
import dk.netarkivet.common.exceptions.IOFailure;
import dk.netarkivet.common.exceptions.NotImplementedException;


public class JMXUtilsTester extends TestCase {
    public JMXUtilsTester(String s) {
        super(s);
    }

    public void setUp() {
    }

    public void tearDown() {
    }
    public void testGetAttribute() throws Exception {
        // Set JMX_timeout to 5 seconds
        Settings.set(Settings.JMX_TIMEOUT, "5");
        TestMBeanServerConnection connection = new TestMBeanServerConnection(0);
        Object ret = connection.getAttribute(JMXUtils.getBeanName("a:aBean=1"), "anAttribute");
        assertEquals("Should have composed bean/attribute name",
                     "a:aBean=1:anAttribute", ret);

        Object o = JMXUtils.getAttribute("a:aBean=1", "anAttribute", connection);
        assertEquals("Should have composed bean/attribute name",
                     "a:aBean=1:anAttribute", o);

        Date then = new Date();
        connection.failCount = JMXUtils.MAX_TRIES + 1;
        try {
            o = JMXUtils.getAttribute("a:aBean=1", "anAttribute", connection);
            fail("Should time out");
        } catch (IOFailure e) {
            //Expected
        }
        Date now = new Date();
        long time = now.getTime()-then.getTime();
        assertTrue("Should take at least 2^" + JMXUtils.MAX_TRIES
                   + " milliseconds, but was " + time + ", should be "
                   + Math.pow(2, JMXUtils.MAX_TRIES),
                   time > Math.pow(2, JMXUtils.MAX_TRIES));
        assertEquals("Should have been called " + JMXUtils.MAX_TRIES + " times.",
                    1,
                    connection.failCount);
    }

    class TestMBeanServerConnection implements MBeanServerConnection {
        int failCount;

        /** Create a test MBeanServerConnection that fails a number of times.
         *
         * @param failCount Number of times the getAttribute/executeCommand
         * methods should be called before they succeed.
         */
        TestMBeanServerConnection(int failCount) {
            this.failCount = failCount;
        }

        public Object getAttribute(ObjectName beanName, String attribute)
                throws InstanceNotFoundException {
            if (failCount-- > 0) {
                throw new InstanceNotFoundException();
            }
            return beanName + ":" + attribute;
        }

        public ObjectInstance createMBean(String className, ObjectName name)
                throws
                ReflectionException,
                InstanceAlreadyExistsException,
                MBeanException,
                NotCompliantMBeanException,
                IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public ObjectInstance createMBean(String className, ObjectName name,
                                          ObjectName loaderName) throws
                                                                 ReflectionException,
                                                                 InstanceAlreadyExistsException,
                                                                 MBeanException,
                                                                 NotCompliantMBeanException,
                                                                 InstanceNotFoundException,
                                                                 IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public ObjectInstance createMBean(String className, ObjectName name,
                                          Object params[], String signature[])
        throws ReflectionException, InstanceAlreadyExistsException,
               MBeanException,
               NotCompliantMBeanException, IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public ObjectInstance createMBean(String className, ObjectName name,
                                          ObjectName loaderName, Object params[],
                                          String signature[]) throws
                                                              ReflectionException,
                                                              InstanceAlreadyExistsException,
                                                              MBeanException,
                                                              NotCompliantMBeanException,
                                                              InstanceNotFoundException,
                                                              IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public void unregisterMBean(ObjectName name) throws
                                                     InstanceNotFoundException,
                                                     MBeanRegistrationException,
                                                     IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public ObjectInstance getObjectInstance(ObjectName name)
        throws InstanceNotFoundException, IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public Set queryMBeans(ObjectName name, QueryExp query) throws IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public Set queryNames(ObjectName name, QueryExp query) throws IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public boolean isRegistered(ObjectName name) throws IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public Integer getMBeanCount() throws IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public AttributeList getAttributes(ObjectName name, String[] attributes)
        throws InstanceNotFoundException, ReflectionException, IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public void setAttribute(ObjectName name, Attribute attribute) throws
                                                                       InstanceNotFoundException,
                                                                       AttributeNotFoundException,
                                                                       InvalidAttributeValueException,
                                                                       MBeanException,
                                                                       ReflectionException,
                                                                       IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public AttributeList setAttributes(ObjectName name,
                                           AttributeList attributes)
        throws InstanceNotFoundException, ReflectionException, IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public Object invoke(ObjectName name, String operationName, Object params[],
                             String signature[]) throws InstanceNotFoundException,
                                                        MBeanException,
                                                        ReflectionException,
                                                        IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public String getDefaultDomain() throws IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public String[] getDomains() throws IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public void addNotificationListener(ObjectName name,
                                            NotificationListener listener,
                                            NotificationFilter filter,
                                            Object handback)
        throws InstanceNotFoundException, IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public void addNotificationListener(ObjectName name, ObjectName listener,
                                            NotificationFilter filter,
                                            Object handback)
        throws InstanceNotFoundException, IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public void removeNotificationListener(ObjectName name, ObjectName listener)
        throws InstanceNotFoundException, ListenerNotFoundException,
               IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public void removeNotificationListener(ObjectName name, ObjectName listener,
                                               NotificationFilter filter,
                                               Object handback) throws
                                                                InstanceNotFoundException,
                                                                ListenerNotFoundException,
                                                                IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public void removeNotificationListener(ObjectName name,
                                               NotificationListener listener)
                throws
                InstanceNotFoundException,
                ListenerNotFoundException,
                IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public void removeNotificationListener(ObjectName name,
                                               NotificationListener listener,
                                               NotificationFilter filter,
                                               Object handback) throws
                                                                InstanceNotFoundException,
                                                                ListenerNotFoundException,
                                                                IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public MBeanInfo getMBeanInfo(ObjectName name) throws
                                                       InstanceNotFoundException,
                                                       IntrospectionException,
                                                       ReflectionException,
                                                       IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }

        public boolean isInstanceOf(ObjectName name, String className)
        throws InstanceNotFoundException, IOException {
            //TODO: implement method
            throw new NotImplementedException("Not implemented");
        }
    }

}
